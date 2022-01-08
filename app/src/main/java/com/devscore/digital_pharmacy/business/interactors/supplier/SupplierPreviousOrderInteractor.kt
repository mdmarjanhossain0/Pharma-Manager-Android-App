package com.devscore.digital_pharmacy.business.interactors.supplier

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.purchases.PurchasesDao
import com.devscore.digital_pharmacy.business.datasource.cache.purchases.toPurchasesOder
import com.devscore.digital_pharmacy.business.datasource.cache.purchases.toPurchasesOrder
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.purchases.PurchasesApiService
import com.devscore.digital_pharmacy.business.datasource.network.purchases.toPurchasesOder
import com.devscore.digital_pharmacy.business.datasource.network.supplier.SupplierApiService
import com.devscore.digital_pharmacy.business.domain.models.AuthToken
import com.devscore.digital_pharmacy.business.domain.models.PurchasesOrder
import com.devscore.digital_pharmacy.business.domain.models.toPurchasesOrderEntity
import com.devscore.digital_pharmacy.business.domain.models.toPurchasesOrderMedicines
import com.devscore.digital_pharmacy.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SupplierPreviousOrderInteractor(
    private val service : SupplierApiService,
    private val cache : PurchasesDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        pk : Int,
        status : Int = 3,
        page: Int
    ): Flow<DataState<List<PurchasesOrder>>> = flow {
        emit(DataState.loading<List<PurchasesOrder>>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        try{
            Log.d(TAG, "Call Api Section")
            val result = service.searchSupplierOrderList(
                "Token ${authToken.token}",
                pk = pk,
                status = status,
                page = page
            )

            Log.d(TAG, result.toString())

            val oderList = result.results.map {
                Log.d(TAG, "looping toLocalMedicine")
                it.toPurchasesOder()
            }

            for(oder in oderList){
                try{
                    Log.d(TAG, "Caching size" + oderList.size.toString())
                    cache.insertPurchasesOrder(oder.toPurchasesOrderEntity())
                    for (medicine in oder.toPurchasesOrderMedicines()) {
                        cache.insertPurchasesOrderMedicine(medicine)
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(
                DataState.error<List<PurchasesOrder>>(
                    response = Response(
                        message = "Unable to update the cache.",
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Error()
                    )
                )
            )
        }
        Log.d(TAG, "Purchases Search Status " + status.toString())

        val successList = cache.getSupplierOrders(
            pk = pk,
            status = status,
            page = page
        ).map {
            it.toPurchasesOder()
        }

        emit(DataState.data(response = null, data = successList))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}