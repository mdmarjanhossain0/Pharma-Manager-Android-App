package com.appbytes.pharma_manager.business.interactors.sales

import android.util.Log
import com.appbytes.pharma_manager.business.datasource.cache.sales.SalesDao
import com.appbytes.pharma_manager.business.datasource.cache.sales.toSalesOder
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.datasource.network.sales.SalesApiService
import com.appbytes.pharma_manager.business.domain.models.AuthToken
import com.appbytes.pharma_manager.business.domain.models.SalesOrder
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SalesOrderLocalDetailsInteractor(
    private val service : SalesApiService,
    private val cache : SalesDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        pk : Int
    ): Flow<DataState<SalesOrder>> = flow {
        emit(DataState.loading<SalesOrder>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        try {
            Log.d(TAG, "Pk " + pk)
            val order = cache.getSalesOrder(pk).toSalesOder()
            emit(DataState.data(response = null, data = order))
        }
        catch (e : java.lang.Exception) {
            e.printStackTrace()
            emit(
                DataState.error<SalesOrder>(
                    response = Response(
                        message = "Order not found or network not available",
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    )
                )
            )
        }

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}