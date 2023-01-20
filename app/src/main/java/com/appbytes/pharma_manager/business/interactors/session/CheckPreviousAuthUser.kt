package com.appbytes.pharma_manager.business.interactors.session

import android.util.Log
import com.appbytes.pharma_manager.business.datasource.cache.account.AccountDao
import com.appbytes.pharma_manager.business.datasource.cache.auth.AuthTokenDao
import com.appbytes.pharma_manager.business.datasource.cache.auth.toAuthToken
import com.appbytes.pharma_manager.business.domain.models.AuthToken
import com.appbytes.pharma_manager.business.domain.util.*
import com.appbytes.pharma_manager.business.domain.util.ErrorHandling.Companion.ERROR_NO_PREVIOUS_AUTH_USER
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * Attempt to authenticate as soon as the user launches the app.
 * If no user was authenticated in a previous session then do nothing.
 */
class CheckPreviousAuthUser(
    private val accountDao: AccountDao,
    private val authTokenDao: AuthTokenDao,
) {
    fun execute(
        email: String,
    ): Flow<DataState<AuthToken>> = flow {
        emit(DataState.loading<AuthToken>())
        var authToken: AuthToken? = null
        val entity = accountDao.searchByEmail(email)
        Log.d("AppDebug", "CheckPreviousAuthUser Cache " + entity.toString())
        if(entity != null){
            Log.d("AppDebug", "CheckPreviousAuthUser Prvious List " + authTokenDao.getAll().toString())
            authToken = authTokenDao.searchByPk(entity.pk)?.toAuthToken()
            Log.d("AppDebug", "CheckPreviousAuthUser Prvious " + authToken.toString())
            if(authToken != null){
                Log.d("AppDebug", "CheckPreviousAuthUser Auth Token " + authToken.toString())
                emit(DataState.data(response = null, data = authToken))
            }
        }
        if(authToken == null){
            throw Exception(ERROR_NO_PREVIOUS_AUTH_USER)
        }
    }.catch{ e ->
        e.printStackTrace()
        emit(returnNoPreviousAuthUser())
    }

    /**
     * If no user was previously authenticated then emit this error. The UI is waiting for it.
     */
    private fun returnNoPreviousAuthUser(): DataState<AuthToken> {
        return DataState.error<AuthToken>(
            response = Response(
                SuccessHandling.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                UIComponentType.None(),
                MessageType.Error()
            )
        )
    }
}












