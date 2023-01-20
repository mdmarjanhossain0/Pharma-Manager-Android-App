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
import com.appbytes.pharma_manager.business.datasource.cache.purchases.toPurchasesOrder
import com.appbytes.pharma_manager.business.datasource.datastore.AppDataStore
import com.appbytes.pharma_manager.business.datasource.network.purchases.PurchasesApiService
import com.appbytes.pharma_manager.business.domain.models.AuthToken
import com.appbytes.pharma_manager.business.domain.models.toCreatePurchasesOrder
import com.appbytes.pharma_manager.business.interactors.purchases.CreatePurchasesOrderInteractor
import com.appbytes.pharma_manager.presentation.session.SessionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

@HiltWorker
class PurchasesWorker @AssistedInject
constructor(
    @Assisted
    context: Context,
    @Assisted
    parameters: WorkerParameters,
    private val database : AppDatabase,
    private var createPurchasesOrderInteractor: CreatePurchasesOrderInteractor,
    var sessionManager : SessionManager,
    val service : PurchasesApiService,
    val appDataStoreManager : AppDataStore,
    val accountDao: AccountDao,
    val authTokenDao: AuthTokenDao
) : Worker(context, parameters) {




    val TAG = "AppDebug"

    override fun doWork(): Result {

        sync()
        return Result.success()
    }

    private fun sync() {
        Log.d(TAG, "Enter successful")
        val orders = database.getPurchasesDao().getSyncData().map {
            it.toPurchasesOrder()
        }



        Log.d(TAG, orders.size.toString())
        Log.d(TAG, orders.toString())
        Log.d(TAG, createPurchasesOrderInteractor.toString())

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
                        for (item in orders) {
                            try {
                                createPurchasesOrderInteractor.execute(
                                    authToken,
                                    item.toCreatePurchasesOrder()
                                ).launchIn(this)
                                database.getPurchasesDao().deleteFailurePurchasesOrder(item.room_id!!)
                            }
                            catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }


        }

        val checks = database.getPurchasesDao().getSyncData().map {
            it.toPurchasesOrder()
        }








        Log.d(TAG, "Size " + checks.size + checks.toString())



    }
}