package com.appbytes.pharma_manager.business.interactors.purchases

import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.datasource.network.purchases.PurchasesApiService
import com.appbytes.pharma_manager.business.datasource.network.purchases.network_response.toPurchasesReturn
import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class PurchasesReturnInteractor (
    private val service : PurchasesApiService,
//    private val cache : SalesDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        createPurchasesReturn : CreatePurchasesReturn
    ): Flow<DataState<PurchasesReturn>> = flow {

        emit(DataState.loading<PurchasesReturn>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        if (createPurchasesReturn.vendor == -1) {
            createPurchasesReturn.vendor = null
        }
        val purchasesOrder = service.createReturnOrder(
            "Token ${authToken.token}",
            createPurchasesReturn
        ).toPurchasesReturn()


        emit(
            DataState.data(response = Response(
                message = "Successfully Uploaded.",
                uiComponentType = UIComponentType.Toast(),
                messageType = MessageType.Success()
            ), data = purchasesOrder))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}





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