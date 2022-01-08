package com.devscore.digital_pharmacy.business.interactors.supplier

import android.content.Context
import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.supplier.SupplierDao
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.supplier.SupplierApiService
import com.devscore.digital_pharmacy.business.datasource.network.supplier.network_response.toSupplier
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class UpdateSupplierInteractor (
    private val service : SupplierApiService,
    private val cache : SupplierDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        pk : Int,
        createSupplier: CreateSupplier
    ): Flow<DataState<Supplier>> = flow {

        emit(DataState.loading<Supplier>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }

            Log.d(TAG, "Call Api Section")
            val supplier = service.updateSupplier(
                "Token ${authToken.token}",
                pk = pk,
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
                    message = "Update successfully.",
                    uiComponentType = UIComponentType.Dialog(),
                    messageType = MessageType.Success()
                ), data = supplier))
            return@flow

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}