package com.devscore.digital_pharmacy.business.interactors.purchases

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.purchases.PurchasesDao
import com.devscore.digital_pharmacy.business.datasource.cache.purchases.toPurchasesOder
import com.devscore.digital_pharmacy.business.datasource.cache.purchases.toPurchasesOrder
import com.devscore.digital_pharmacy.business.datasource.cache.sales.SalesDao
import com.devscore.digital_pharmacy.business.datasource.cache.sales.toSalesOder
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.purchases.PurchasesApiService
import com.devscore.digital_pharmacy.business.datasource.network.purchases.toPurchasesOder
import com.devscore.digital_pharmacy.business.datasource.network.sales.SalesApiService
import com.devscore.digital_pharmacy.business.datasource.network.sales.toSalesOrder
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SearchPurchasesOrder(
    private val service : PurchasesApiService,
    private val cache : PurchasesDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        query: String,
        status : Int,
        page: Int
    ): Flow<DataState<List<PurchasesOrder>>> = flow {
        emit(DataState.loading<List<PurchasesOrder>>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }
        if (status == 0) {
            val success = cache.searchGenerateOrderWithMedicine(
                page = page
            ).map {
                it.toPurchasesOder()
            }

            val failure = cache.searchFailurePurchasesOrderWithMedicine().map {
                it.toPurchasesOrder()
            }
            val  preList = marge(success, failure)
            emit(DataState.data(response = null, data = preList))
        }
        else {
            val success = cache.searchCompleteOrderWithMedicine(
                query = query,
                status = 3,
                page = page
            ).map {
                it.toPurchasesOder()
            }
            emit(DataState.data(response = null, data = success))
        }
        emit(DataState.loading<List<PurchasesOrder>>())

        try{
            Log.d(TAG, "Call Api Section")
            val result = service.searchPurchasesOder(
                "Token ${authToken.token}",
                query = query,
                status = status,
                page = page
            )

            Log.d(TAG, result.toString())

            val oderList = result.results.map {
                it.toPurchasesOder()
            }

            for(oder in oderList){
                try{
                    Log.d(TAG, "Purchases Caching size" + oderList.size.toString())
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
        if (status == 0) {
            val success = cache.searchGenerateOrderWithMedicine(
                page = page
            ).map {
                it.toPurchasesOder()
            }

            val failure = cache.searchFailurePurchasesOrderWithMedicine().map {
                it.toPurchasesOrder()
            }
            val  preList = marge(success, failure)
            emit(DataState.data(response = null, data = preList))
        }
        else {
            val success = cache.searchCompleteOrderWithMedicine(
                query = query,
                status = 3,
                page = page
            ).map {
                it.toPurchasesOder()
            }
            emit(DataState.data(response = null, data = success))
        }
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}

fun marge(successList: List<PurchasesOrder>, failureList : List<PurchasesOrder>) : List<PurchasesOrder> {
    val allMedicine  = mutableListOf<PurchasesOrder>()
    allMedicine.addAll(failureList)
    allMedicine.addAll(successList)
    return allMedicine
}