package com.appbytes.pharma_manager.business.interactors.inventory.local

import android.util.Log
import com.appbytes.pharma_manager.business.datasource.cache.shortlist.ShortListDao
import com.appbytes.pharma_manager.business.datasource.network.GenericResponse
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.datasource.network.shortlist.ShortListApiService
import com.appbytes.pharma_manager.business.domain.models.AuthToken
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class DeleteShortListInteractor (
    private val service : ShortListApiService,
    private val cache : ShortListDao
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
        pk : Int
    ): Flow<DataState<GenericResponse>> = flow {

        emit(DataState.loading<GenericResponse>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }




        Log.d(TAG, "Call Api Section")
        val response = service.deleteShortList(
            "Token ${authToken.token}",
            pk
        )
        cache.deleteShortList(pk = pk)
        emit(
            DataState.data(response = Response(
                message = "Delete Successfully.",
                uiComponentType = UIComponentType.Toast(),
                messageType = MessageType.Success()
            ), data = response))

    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}