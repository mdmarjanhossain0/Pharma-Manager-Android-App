package com.devscore.digital_pharmacy.business.interactors.sales

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.sales.SalesDao
import com.devscore.digital_pharmacy.business.datasource.network.GenericResponse
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.sales.SalesApiService
import com.devscore.digital_pharmacy.business.datasource.network.sales.network_response.toSalesOrder
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class DeleteSalesOrderInteractor (
    private val service : SalesApiService,
    private val cache : SalesDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        order : SalesOrder
    ): Flow<DataState<GenericResponse>> = flow {

        emit(DataState.loading<GenericResponse>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }




        Log.d(TAG, "Call Api Section")
        Log.d(TAG, order.toString())
        if (order.pk!! > 0) {
            val response = service.salesOrderDelete(
                "Token ${authToken.token}",
                order.pk!!
            )
            cache.deleteSalesOder(pk = order.pk!!)
            emit(
                DataState.data(response = Response(
                    message = "Delete Successfully.",
                    uiComponentType = UIComponentType.Toast(),
                    messageType = MessageType.Success()
                ), data = response))
        }
        else {
            cache.deleteFailureSalesOder(order.room_id!!)
            emit(
                DataState.data(response = Response(
                    message = "Delete Successfully.",
                    uiComponentType = UIComponentType.Toast(),
                    messageType = MessageType.Success()
                ), data = GenericResponse(
                    response = "Delete Successfully",
                    errorMessage = null
                )))
        }

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}