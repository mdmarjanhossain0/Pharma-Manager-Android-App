package com.appbytes.pharma_manager.business.interactors.cashregister

import android.util.Log
import com.appbytes.pharma_manager.business.datasource.cache.cashregister.ReceiveDao
import com.appbytes.pharma_manager.business.datasource.network.cashregister.CashRegisterApiService
import com.appbytes.pharma_manager.business.datasource.network.cashregister.network_response.toReceive
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CreateReceiveInteractor (
    private val service : CashRegisterApiService,
    private val cache : ReceiveDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        createReceive: CreateReceive
    ): Flow<DataState<Receive>> = flow {

        emit(DataState.loading<Receive>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        if (createReceive.customer == -1) {
            createReceive.customer = null
        }

        if (createReceive.vendor == -1) {
            createReceive.vendor = null
        }

        try{
            Log.d(TAG, "Call Api Section")
            val receive = service.createReceive(
                "Token ${authToken.token}",
                createReceive
            ).toReceive()


            try{
                cache.insertReceive(receive.toReceiveEntity())
            }catch (e: Exception){
                e.printStackTrace()

            }

            emit(
                DataState.data(response = Response(
                    message = "Successfully Uploaded.",
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Success()
                ), data = receive))
            return@flow


        } catch (e: Exception){
            e.printStackTrace()

            try{
                cache.insertFailureReceive(createReceive.toReceive().toFailureReceive())
            } catch (e: Exception){
                e.printStackTrace()
            }


            emit(
                DataState.error<Receive>(
                    response = Response(
                        message = "Unable to create Sales Oder. Please be careful and don't uninstall or log out",
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    )
                )
            )
            return@flow
        }

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}