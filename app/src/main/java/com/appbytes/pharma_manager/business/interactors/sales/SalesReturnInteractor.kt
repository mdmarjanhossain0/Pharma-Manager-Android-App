package com.appbytes.pharma_manager.business.interactors.sales

import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.datasource.network.sales.SalesApiService
import com.appbytes.pharma_manager.business.datasource.network.sales.network_response.toSalesReturn
import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SalesReturnInteractor (
    private val service : SalesApiService,
//    private val cache : SalesDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        createSalesReturn: CreateSalesReturn
    ): Flow<DataState<SalesReturn>> = flow {

        emit(DataState.loading<SalesReturn>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        if (createSalesReturn.customer == -1) {
            createSalesReturn.customer = null
        }
        val salesOder = service.createReturnOrder(
            "Token ${authToken.token}",
            createSalesReturn
        ).toSalesReturn()

        /*try{
            Log.d(TAG, "Call Api Section")
            val salesOder = service.createReturnOrder(
                "Token ${authToken.token}",
                createSalesReturn
            ).toSalesReturn()


            emit(
                DataState.data(response = Response(
                    message = "Successfully Uploaded.",
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Success()
                ), data = salesOder))
            return@flow


        } catch (e: Exception){
            e.printStackTrace()

            emit(
                DataState.data(response = Response(
                    message = "Successfully Uploaded.",
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Success()
                ), data = createSalesReturn.toSalesReturn()))
            return@flow
        }*/


        emit(
            DataState.data(response = Response(
                message = "Successfully Uploaded.",
                uiComponentType = UIComponentType.Toast(),
                messageType = MessageType.Success()
            ), data = salesOder))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}