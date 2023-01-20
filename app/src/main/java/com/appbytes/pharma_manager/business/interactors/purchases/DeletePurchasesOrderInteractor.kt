package com.appbytes.pharma_manager.business.interactors.purchases

import android.util.Log
import com.appbytes.pharma_manager.business.datasource.cache.purchases.PurchasesDao
import com.appbytes.pharma_manager.business.datasource.network.GenericResponse
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.datasource.network.purchases.PurchasesApiService
import com.appbytes.pharma_manager.business.domain.models.AuthToken
import com.appbytes.pharma_manager.business.domain.models.PurchasesOrder
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class DeletePurchasesOrderInteractor (
    private val service : PurchasesApiService,
    private val cache : PurchasesDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        order : PurchasesOrder
    ): Flow<DataState<GenericResponse>> = flow {

        emit(DataState.loading<GenericResponse>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

            Log.d(TAG, "Call Api Section")
        if (order.pk == null || order.pk < 1) {

            cache.deleteFailurePurchasesOrder(order.room_id!!)
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
        else {






            val response = service.purchasesOrderDelete(
                "Token ${authToken.token}",
                order.pk!!
            )

            cache.deletePurchasesOrder(pk = order.pk)
            emit(
                DataState.data(response = Response(
                    message = "Delete Successfully.",
                    uiComponentType = UIComponentType.Toast(),
                    messageType = MessageType.Success()
                ), data = response))
        }

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}