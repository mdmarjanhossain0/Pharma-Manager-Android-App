package com.devscore.digital_pharmacy.business.interactors.supplier

import android.content.Context
import android.util.Log
import androidx.work.*
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.*
import com.devscore.digital_pharmacy.business.datasource.cache.supplier.SupplierDao
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryApiService
import com.devscore.digital_pharmacy.business.datasource.network.inventory.network_responses.toLocalMedicine
import com.devscore.digital_pharmacy.business.datasource.network.supplier.SupplierApiService
import com.devscore.digital_pharmacy.business.datasource.network.supplier.network_response.toSupplier
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import com.devscore.digital_pharmacy.presentation.util.CustomerWorker
import com.devscore.digital_pharmacy.presentation.util.SupplierWorker
import com.devscore.digital_pharmacy.presentation.util.UploadWorker
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