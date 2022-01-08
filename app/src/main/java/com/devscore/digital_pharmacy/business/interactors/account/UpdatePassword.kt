package com.devscore.digital_pharmacy.business.interactors.account

import com.devscore.digital_pharmacy.business.datasource.network.account.AccountApiService
import com.devscore.digital_pharmacy.business.datasource.network.handleUseCaseException
import com.devscore.digital_pharmacy.business.domain.models.AuthToken
import com.devscore.digital_pharmacy.business.domain.util.*
import com.devscore.digital_pharmacy.business.domain.util.ErrorHandling.Companion.ERROR_UPDATE_PASSWORD
import com.devscore.digital_pharmacy.business.domain.util.ErrorHandling.Companion.GENERIC_ERROR
import com.devscore.digital_pharmacy.business.domain.util.SuccessHandling.Companion.SUCCESS_PASSWORD_UPDATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class UpdatePassword(
    private val service: AccountApiService,
) {
    fun execute(
        authToken: AuthToken?,
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String,
    ): Flow<DataState<Response>> = flow {
        emit(DataState.loading<Response>())
        if(authToken == null){
            throw Exception(ErrorHandling.ERROR_AUTH_TOKEN_INVALID)
        }
        // Update network
        val response = service.updatePassword(
            authorization = "Token ${authToken.token}",
            currentPassword = currentPassword,
            newPassword = newPassword,
            confirmNewPassword = confirmNewPassword
        )

        if(response.response == GENERIC_ERROR){
            throw Exception(response.errorMessage)
        }
        else if(response.response != SUCCESS_PASSWORD_UPDATED){
            throw Exception(ERROR_UPDATE_PASSWORD)
        }

        // Tell the UI it was successful
        emit(DataState.data<Response>(
            data = Response(
                message = SUCCESS_PASSWORD_UPDATED,
                uiComponentType = UIComponentType.None(),
                messageType = MessageType.Success()
            ),
            response = Response(
                message = SUCCESS_PASSWORD_UPDATED,
                uiComponentType = UIComponentType.Dialog(),
                messageType = MessageType.Success()
            )
        ))





//        emit(DataState.data(response = Response(
//            message = SUCCESS_PASSWORD_UPDATED,
//            uiComponentType = UIComponentType.Dialog(),
//            messageType = MessageType.Error()
//        ), data = Response(
//            message = SUCCESS_PASSWORD_UPDATED,
//            uiComponentType = UIComponentType.Dialog(),
//            messageType = MessageType.Error()
//        )))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}
