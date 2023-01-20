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
import com.appbytes.pharma_manager.business.datasource.cache.supplier.toSupplier
import com.appbytes.pharma_manager.business.datasource.datastore.AppDataStore
import com.appbytes.pharma_manager.business.datasource.network.supplier.SupplierApiService
import com.appbytes.pharma_manager.business.domain.models.AuthToken
import com.appbytes.pharma_manager.business.domain.models.toCreateSupplier
import com.appbytes.pharma_manager.business.interactors.supplier.CreateSupplierInteractor
import com.appbytes.pharma_manager.presentation.session.SessionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

@HiltWorker
class SupplierWorker @AssistedInject
constructor(
    @Assisted
    context: Context,
    @Assisted
    parameters: WorkerParameters,
    private val database : AppDatabase,
    private var createSupplierInteractor: CreateSupplierInteractor,
    var sessionManager : SessionManager,
    val service : SupplierApiService,
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
        val supplier = database.getSupplierDao().getSyncData()



        Log.d(TAG, supplier.size.toString())
        Log.d(TAG, supplier.toString())
        Log.d(TAG, createSupplierInteractor.toString())

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
                        for (item in supplier) {
                            try {
                                createSupplierInteractor.execute(
                                    authToken,
                                    item.toSupplier().toCreateSupplier()
                                ).launchIn(this)
                                database.getCustomerDao().deleteFailureCustomer(item.room_id!!)
                                val checks = database.getSupplierDao().getSyncData()
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