package com.devscore.digital_pharmacy.business.interactors.sales

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.sales.SalesDao
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.sales.SalesApiService
import com.devscore.digital_pharmacy.business.datasource.network.sales.network_response.toSalesOrder
import com.devscore.digital_pharmacy.business.datasource.network.sales.network_response.toSalesReturn
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
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
                uiComponentType = UIComponentType.Dialog(),
                messageType = MessageType.Success()
            ), data = salesOder))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}