package com.devscore.digital_pharmacy.business.interactors.inventory.local

import android.util.Log
import com.devscore.digital_pharmacy.business.datasource.cache.sales.SalesDao
import com.devscore.digital_pharmacy.business.datasource.cache.shortlist.ShortListDao
import com.devscore.digital_pharmacy.business.datasource.network.GenericResponse
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryApiService
import com.devscore.digital_pharmacy.business.datasource.network.sales.SalesApiService
import com.devscore.digital_pharmacy.business.datasource.network.shortlist.ShortListApiService
import com.devscore.digital_pharmacy.business.domain.models.AuthToken
import com.devscore.digital_pharmacy.business.domain.util.*
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