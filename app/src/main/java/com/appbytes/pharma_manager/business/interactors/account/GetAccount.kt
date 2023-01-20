package com.appbytes.pharma_manager.business.interactors.account

import android.util.Log
import com.appbytes.pharma_manager.business.datasource.cache.account.*
import com.appbytes.pharma_manager.business.datasource.cache.auth.AuthTokenDao
import com.appbytes.pharma_manager.business.datasource.cache.auth.toEntity
import com.appbytes.pharma_manager.business.datasource.datastore.AppDataStore
import com.appbytes.pharma_manager.business.datasource.network.auth.AuthService
import com.appbytes.pharma_manager.business.datasource.network.handleUseCaseException
import com.appbytes.pharma_manager.business.domain.models.Account
import com.appbytes.pharma_manager.business.domain.models.AuthToken
import com.appbytes.pharma_manager.business.domain.util.*
import com.appbytes.pharma_manager.business.domain.util.ErrorHandling.Companion.ERROR_AUTH_TOKEN_INVALID
import com.appbytes.pharma_manager.business.domain.util.ErrorHandling.Companion.ERROR_UNABLE_TO_RETRIEVE_ACCOUNT_DETAILS
import com.appbytes.pharma_manager.presentation.util.DataStoreKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class GetAccount(
    private val service: AuthService,
    private val cache: AccountDao,
    private val authTokenDao: AuthTokenDao,
    private val appDataStoreManager: AppDataStore
) {
    private val TAG: String = "AppDebug"

    fun execute(
        authToken: AuthToken?,
    ): Flow<DataState<Account>> = flow {
        emit(DataState.loading<Account>())
        if(authToken == null){
            throw Exception(ERROR_AUTH_TOKEN_INVALID)
        }

        val email = appDataStoreManager.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)

        // emit from cache
        val cachedAccount = cache.searchByEmail(email!!)?.toAccount()
        emit(DataState.data(response = null, data = cachedAccount))


        Log.d(TAG, "Cache Data " + cachedAccount)

        try {
            val account = service.getAccount("Token ${authToken.token}")
            Log.d(TAG, "Fetch From Network Account " +account.toString())
            Log.d(TAG, "Fetch From Network Account pk " +account.pk)


            Log.d(TAG, "All Accounts From Cache Account " +cache.getAll())
            Log.d("AppDebug", "CheckPreviousAuthUser Prvious " + authToken.toString())
            cache.insertAndReplace(
                Account(
                    account.pk,
                    account.email.toLowerCase(),
                    account.shop_name,
                    account.username,
                    account.profile_picture,
                    account.mobile,
                    account.license_key,
                    account.address,
                    account.is_employee,
                    account.role,
                    account.file
                ).toEntity()
            )

            Log.d("AppDebug", "CheckPreviousAuthUser Prvious " + authToken.toString())




            // cache the auth token
            val authToken = AuthToken(
                account.pk,
                account.token
            )
            val result = authTokenDao.insert(authToken.toEntity())

            if(result < 0){
                throw java.lang.Exception(ErrorHandling.ERROR_SAVE_AUTH_TOKEN)
            }
            // save authenticated user to datastore for auto-login next time
            appDataStoreManager.setValue(DataStoreKeys.PREVIOUS_AUTH_USER, email)


            Log.d(TAG, "Cache Data " + cachedAccount)

            Log.d(TAG, "All Accounts From Cache Account " +cache.getAll())



//            appDataStoreManager.setValue(DataStoreKeys.PREVIOUS_AUTH_USER, account.email)
        }
        catch (e : Exception) {
            e.printStackTrace()
        }


//        val email = appDataStoreManager.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)
//
//        // emit from cache
//        val cachedAccount = cache.searchByEmail(email!!)?.toAccount()

        if(cachedAccount == null){
            throw Exception(ERROR_UNABLE_TO_RETRIEVE_ACCOUNT_DETAILS)
        }



        Log.d("AppDebug", "CheckPreviousAuthUser Prvious " + authToken.toString())
        emit(DataState.data(response = null, data = cachedAccount))
    }.catch { e ->
        emit(handleUseCaseException(e))
    }
}