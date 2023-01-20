package com.appbytes.pharma_manager.presentation.util

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.appbytes.pharma_manager.business.datasource.cache.AppDatabase
import com.appbytes.pharma_manager.business.datasource.cache.account.AccountDao
import com.appbytes.pharma_manager.business.datasource.cache.auth.AuthTokenDao
import com.appbytes.pharma_manager.business.datasource.cache.auth.toAuthToken
import com.appbytes.pharma_manager.business.datasource.cache.customer.toCustomer
import com.appbytes.pharma_manager.business.datasource.datastore.AppDataStore
import com.appbytes.pharma_manager.business.datasource.network.customer.CustomerApiService
import com.appbytes.pharma_manager.business.domain.models.AuthToken
import com.appbytes.pharma_manager.business.domain.models.toCreateCustomer
import com.appbytes.pharma_manager.business.interactors.customer.CreateCustomerInteractor
import com.appbytes.pharma_manager.presentation.session.SessionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

@HiltWorker
class CustomerWorker @AssistedInject
constructor(
    @Assisted
    context: Context,
    @Assisted
    parameters: WorkerParameters,
    private val database : AppDatabase,
    private var createCustomerInteractor: CreateCustomerInteractor,
    var sessionManager : SessionManager,
    val service : CustomerApiService,
    val appDataStoreManager : AppDataStore,
    val accountDao: AccountDao,
    val authTokenDao: AuthTokenDao
) : Worker(context, parameters) {




    val TAG = "AppDebug"

    override fun doWork(): Result {

//        sync()
        return Result.success()
    }

    private fun sync() {
        Log.d(TAG, "Enter successful")
        val customers = database.getCustomerDao().getSyncData()



        Log.d(TAG, customers.size.toString())
        Log.d(TAG, customers.toString())
        Log.d(TAG, createCustomerInteractor.toString())

        Log.d(TAG, "SessionManager " + sessionManager.toString())
        Log.d(TAG, "Token " + sessionManager.state.value?.authToken?.token.toString())

        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, Thread.currentThread().name.toString())
            appDataStoreManager.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)?.let { email ->
                var authToken: AuthToken? = null
                val entity = accountDao.searchByEmail(email)
                if(entity != null){
                    authToken = authTokenDao.searchByPk(entity.pk)?.toAuthToken()
                    if(authToken != null){
                        for (item in customers) {
                            try {
                                createCustomerInteractor.execute(
                                    authToken,
                                    item.toCustomer().toCreateCustomer()
                                ).launchIn(this)
                                database.getCustomerDao().deleteFailureCustomer(item)
                                val checks = database.getCustomerDao().getSyncData()
                                Log.d(TAG, "Size " + checks.size + checks.toString())
                            }
                            catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

        }



    }
}