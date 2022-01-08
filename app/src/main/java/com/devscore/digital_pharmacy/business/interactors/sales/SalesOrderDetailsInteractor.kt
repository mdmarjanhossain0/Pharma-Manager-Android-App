package com.devscore.digital_pharmacy.business.interactors.sales

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.LocalMedicineDao
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.toLocalMedicine
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.toLocalMedicineEntity
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.toLocalMedicineUnitEntity
import com.devscore.digital_pharmacy.business.datasource.cache.sales.SalesDao
import com.devscore.digital_pharmacy.business.datasource.cache.sales.toSalesOder
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.inventory.network_responses.toList
import com.devscore.digital_pharmacy.business.datasource.network.sales.SalesApiService
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SalesOrderDetailsInteractor(
    private val service : SalesApiService,
    private val cache : SalesDao,
    private val localCache: LocalMedicineDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        pk : Int
    ): Flow<DataState<SalesReturnOrder>> = flow {
        emit(DataState.loading<SalesReturnOrder>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }
        var order : SalesOrder? = null

        try {
            order = cache.getSalesOrder(pk).toSalesOder()
            val medicines = service.searchLocalMedicineList(
                "Token ${authToken.token}",
                pk
            ).toList()

            for (item in medicines) {
                localCache.insertLocalMedicine(item.toLocalMedicineEntity())
                for (unit in item.toLocalMedicineUnitEntity()) {
                    localCache.insertLocalMedicineUnit(unit)
                }
            }
            val orderWithMedicine = SalesReturnOrder(
                order = order,
                medicineList = medicines
            )
            Log.d(TAG, orderWithMedicine.toString())
            emit(DataState.data(response = null, data = orderWithMedicine))
            return@flow
        }
        catch (e : java.lang.Exception) {
            e.printStackTrace()
            try {
                val list = mutableListOf<LocalMedicine>()
                if (order == null) {
                    order = cache.getSalesOrder(pk).toSalesOder()
                }
                for (item in  order.sales_oder_medicines!!) {
                    val medicine = localCache.getMedicineById(item.local_medicine)?.toLocalMedicine()
                    if (medicine != null) {
                        list.add(medicine)
                    }
                    else {
                        throw Exception("not found in cache")
                    }
                }
                val orderWithMedicine = SalesReturnOrder(
                    order = order,
                    medicineList = list
                )
                emit(DataState.data(response = null, data = orderWithMedicine))
                return@flow
            }
            catch (e : Exception) {

            }
        }

        emit(
            DataState.data<SalesReturnOrder>(
                response = Response(
                    message = "Order not found or network not available",
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                ),
                data = SalesReturnOrder(
                    order = order!!,
                    medicineList = null
                )
            )
        )

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}
