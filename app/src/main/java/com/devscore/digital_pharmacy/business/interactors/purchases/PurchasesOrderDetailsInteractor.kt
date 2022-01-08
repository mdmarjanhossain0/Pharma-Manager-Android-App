package com.devscore.digital_pharmacy.business.interactors.purchases

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.LocalMedicineDao
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.toLocalMedicine
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.toLocalMedicineEntity
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.toLocalMedicineUnitEntity
import com.devscore.digital_pharmacy.business.datasource.cache.purchases.PurchasesDao
import com.devscore.digital_pharmacy.business.datasource.cache.purchases.toPurchasesOder
import com.devscore.digital_pharmacy.business.datasource.cache.sales.SalesDao
import com.devscore.digital_pharmacy.business.datasource.cache.sales.toSalesOder
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.inventory.network_responses.toList
import com.devscore.digital_pharmacy.business.datasource.network.purchases.PurchasesApiService
import com.devscore.digital_pharmacy.business.datasource.network.sales.SalesApiService
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class PurchasesOrderDetailsInteractor (
    private val service : PurchasesApiService,
    private val cache : PurchasesDao,
    private val localCache: LocalMedicineDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        pk : Int
    ): Flow<DataState<PurchasesReturnOrder>> = flow {
        emit(DataState.loading<PurchasesReturnOrder>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }
        var order : PurchasesOrder? = null

        try {
            order = cache.getPurchasesOrder(pk).toPurchasesOder()
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
            val orderWithMedicine = PurchasesReturnOrder(
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
                    order = cache.getPurchasesOrder(pk).toPurchasesOder()
                }
                for (item in  order.purchases_order_medicines!!) {
                    val medicine = localCache.getMedicineById(item.local_medicine)?.toLocalMedicine()
                    if (medicine != null) {
                        list.add(medicine)
                    }
                    else {
                        throw Exception("not found in cache")
                    }
                }
                val orderWithMedicine = PurchasesReturnOrder(
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
            DataState.data<PurchasesReturnOrder>(
                response = Response(
                    message = "Order not found or network not available",
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Error()
                ),
                data = PurchasesReturnOrder(
                    order = order!!,
                    medicineList = null
                )
            )
        )

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}