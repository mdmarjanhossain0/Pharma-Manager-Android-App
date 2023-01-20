package com.appbytes.pharma_manager.business.interactors.customer

import android.util.Log
import com.appbytes.pharma_manager.business.datasource.cache.sales.SalesDao
import com.appbytes.pharma_manager.business.datasource.cache.sales.toSalesOder
import com.appbytes.pharma_manager.business.datasource.network.customer.CustomerApiService
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.datasource.network.sales.toSalesOrder
import com.appbytes.pharma_manager.business.domain.models.AuthToken
import com.appbytes.pharma_manager.business.domain.models.SalesOrder
import com.appbytes.pharma_manager.business.domain.models.toSalesOrderEntity
import com.appbytes.pharma_manager.business.domain.models.toSalesOrderMedicinesEntity
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CustomerPreviousOrderInteractor(
    private val service : CustomerApiService,
    private val cache : SalesDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        pk : Int,
        status : Int = 3,
        page: Int
    ): Flow<DataState<List<SalesOrder>>> = flow {
        emit(DataState.loading<List<SalesOrder>>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        try{
            Log.d(TAG, "Call Api Section")
            val result = service.customerOrderList(
                "Token ${authToken.token}",
                pk = pk,
                status = status,
                page = page
            )

            Log.d(TAG, result.toString())

            val oderList = result.results.map {
                Log.d(TAG, "looping toLocalMedicine")
                it.toSalesOrder()
            }

            for(oder in oderList){
                try{
                    Log.d(TAG, "Caching size" + oderList.size.toString())
                    cache.insertSalesOder(oder.toSalesOrderEntity())
                    for (medicine in oder.toSalesOrderMedicinesEntity()) {
                        cache.insertSaleOderMedicine(medicine)
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(
                DataState.error<List<SalesOrder>>(
                    response = Response(
                        message = "Unable to update the cache.",
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Error()
                    )
                )
            )
        }

        val successList = cache.searchCustomerOrders(
            pk = pk,
            status = status,
            page = page
        ).map { it.toSalesOder() }





        emit(DataState.data(response = null, data =  successList))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}