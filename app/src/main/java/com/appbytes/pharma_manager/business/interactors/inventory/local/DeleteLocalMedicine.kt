package com.appbytes.pharma_manager.business.interactors.inventory.local

import android.util.Log
import com.appbytes.pharma_manager.business.datasource.cache.inventory.local.LocalMedicineDao
import com.appbytes.pharma_manager.business.datasource.network.GenericResponse
import com.appbytes.pharma_manager.business.datasource.network.inventory.InventoryApiService
import com.appbytes.pharma_manager.business.domain.models.AuthToken
import com.appbytes.pharma_manager.business.domain.models.LocalMedicine
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class DeleteLocalMedicine (
    private val service : InventoryApiService,
    private val cache : LocalMedicineDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        localMedicine : LocalMedicine
    ): Flow<DataState<GenericResponse>> = flow {

        emit(DataState.loading<GenericResponse>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }




        Log.d(TAG, "Call Api Section")
        Log.d(TAG, localMedicine.toString())
        if (localMedicine.id == null || localMedicine.id!! < 1) {
            cache.deleteFailureLocalMedicine(localMedicine.room_medicine_id!!)
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
            val response = service.deleteMedicine(
                "Token ${authToken.token}",
                id = localMedicine.id!!
            )
            cache.deleteLocalMedicine(id = localMedicine.id!!)
            emit(
                DataState.data(response = Response(
                    message = "Delete Successfully.",
                    uiComponentType = UIComponentType.Toast(),
                    messageType = MessageType.Success()
                ), data = response))
        }

    }.catch { e ->
        emit(com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException(e))
    }
}