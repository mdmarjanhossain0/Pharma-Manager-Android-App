package com.appbytes.pharma_manager.business.interactors.supplier

import android.util.Log
import com.appbytes.pharma_manager.business.datasource.cache.supplier.SupplierDao
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.datasource.network.supplier.SupplierApiService
import com.appbytes.pharma_manager.business.datasource.network.supplier.network_response.toSupplier
import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CreateSupplierInteractor (
    private val service : SupplierApiService,
    private val cache : SupplierDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        createSupplier: CreateSupplier
    ): Flow<DataState<Supplier>> = flow {

        emit(DataState.loading<Supplier>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

        try{
            Log.d(TAG, "Call Api Section")
            val supplier = service.createSupplier(
                "Token ${authToken.token}",
                createSupplier
            ).toSupplier()


            Log.d(TAG, "CreateSupplierInteractior " + supplier.toString())

            try{
                cache.insertSupplier(supplier.toSupplierEntity())
            }catch (e: Exception){
                e.printStackTrace()
            }

            emit(
                DataState.data(response = Response(
                    message = "Successfully Uploaded.",
                    uiComponentType = UIComponentType.Toast(),
                    messageType = MessageType.Success()
                ), data = supplier))
            return@flow


        }



        catch (e: Exception){
            e.printStackTrace()

            try{
                val draftSupplier = createSupplier.toSupplier().copy(
                    room_id = null
                )
                cache.insertFailureSupplier(draftSupplier.toFailureSupplierEntity())
            } catch (e: Exception){
                e.printStackTrace()
            }
        }


        val stateSupplier = createSupplier.toSupplier()

        emit(
            DataState.data(response = Response(
                message = "Create a draft supplier. Please be careful and don't uninstall or log out",
                uiComponentType = UIComponentType.Dialog(),
                messageType = MessageType.Error()
            ), data = stateSupplier))

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}