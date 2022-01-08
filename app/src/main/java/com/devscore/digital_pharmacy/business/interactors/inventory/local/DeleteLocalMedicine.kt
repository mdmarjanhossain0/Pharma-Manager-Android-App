package com.devscore.digital_pharmacy.business.interactors.inventory.local

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.LocalMedicineDao
import com.devscore.digital_pharmacy.business.datasource.cache.shortlist.ShortListDao
import com.devscore.digital_pharmacy.business.datasource.network.GenericResponse
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryApiService
import com.devscore.digital_pharmacy.business.datasource.network.shortlist.ShortListApiService
import com.devscore.digital_pharmacy.business.domain.models.AuthToken
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.util.*
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
        emit(com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException(e))
    }
}