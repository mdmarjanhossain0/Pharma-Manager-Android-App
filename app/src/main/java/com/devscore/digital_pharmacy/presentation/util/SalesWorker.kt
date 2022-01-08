package com.devscore.digital_pharmacy.presentation.util

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.devscore.digital_pharmacy.business.datasource.cache.AppDatabase
import com.devscore.digital_pharmacy.business.datasource.cache.account.AccountDao
import com.devscore.digital_pharmacy.business.datasource.cache.auth.AuthTokenDao
import com.devscore.digital_pharmacy.business.datasource.cache.auth.toAuthToken
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.toLocalMedicine
import com.devscore.digital_pharmacy.business.datasource.cache.sales.toSalesOder
import com.devscore.digital_pharmacy.business.datasource.datastore.AppDataStore
import com.devscore.digital_pharmacy.business.datasource.network.sales.SalesApiService
import com.devscore.digital_pharmacy.business.domain.models.AuthToken
import com.devscore.digital_pharmacy.business.domain.models.toCreateSalesOrder
import com.devscore.digital_pharmacy.business.interactors.sales.CreateFailureSalesInteractor
import com.devscore.digital_pharmacy.business.interactors.sales.CreateSalesOderInteractor
import com.devscore.digital_pharmacy.presentation.session.SessionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

@HiltWorker
class SalesWorker @AssistedInject
constructor(
    @Assisted
    context: Context,
    @Assisted
    parameters: WorkerParameters,
    private val database : AppDatabase,
    private var createSalesOderInteractor: CreateSalesOderInteractor,
    private var interactor : CreateFailureSalesInteractor,
    var sessionManager : SessionManager,
    val service : SalesApiService,
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
        val orders = database.getSalesDao().getSyncData().map {
            it.toSalesOder()
        }



        Log.d(TAG, orders.size.toString())
        Log.d(TAG, orders.toString())
        Log.d(TAG, createSalesOderInteractor.toString())

        Log.d(TAG, "SessionManager " + sessionManager.toString())
        Log.d(TAG, "Token " + sessionManager.state.value?.authToken?.token.toString())

        var call : Boolean = true
        if (orders.size > 0) {
            CoroutineScope(Dispatchers.IO).launch {
                Log.d(TAG, Thread.currentThread().name.toString())
                appDataStoreManager.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)?.let { email ->
                    var authToken: AuthToken? = null
                    val entity = accountDao.searchByEmail(email)
                    if(entity != null){
                        authToken = authTokenDao.searchByPk(entity.pk)?.toAuthToken()
                        if(authToken != null && call){
                            call = false
                            for (item in orders) {
                                try {
                                    database.getSalesDao().deleteFailureSalesOder(item.room_id!!)
                                }
                                catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            interactor.execute(
                                authToken,
                                orders
                            ).launchIn(this)
                        }
                    }
                }


            }
        }

        val checks = database.getSalesDao().getSyncData().map {
            it.toSalesOder()
        }








        Log.d(TAG, "Size " + checks.size + checks.toString())



    }
}