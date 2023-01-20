package com.appbytes.pharma_manager.business.interactors.report

import android.util.Log
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.datasource.network.report.ReportApiService
import com.appbytes.pharma_manager.business.datasource.network.report.network_response.toNotification
import com.appbytes.pharma_manager.business.domain.models.AuthToken
import com.appbytes.pharma_manager.business.domain.models.Notification
import com.appbytes.pharma_manager.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetNotification(
    private val service : ReportApiService
) {

    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?
    ): Flow<DataState<List<Notification>>> = flow {
        emit(DataState.loading<List<Notification>>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }


        try {
            val reportResponse = service.notification(
                "Token ${authToken.token}"
            )

            val report = reportResponse.results.map {
                Log.d(TAG, "looping toLocalMedicine")
                it.toNotification()
            }
            emit(DataState.data(response = null, data = report))

        }catch (e: Exception){
            e.printStackTrace()
            print(e.message)
            print(e.localizedMessage)
            emit(
                DataState.error<List<Notification>>(
                    response = Response(
                        message = e.message,
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Error()
                    )
                )
            )
        }
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}