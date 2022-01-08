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
import com.devscore.digital_pharmacy.business.datasource.datastore.AppDataStore
import com.devscore.digital_pharmacy.business.datasource.network.inventory.InventoryApiService
import com.devscore.digital_pharmacy.business.domain.models.AuthToken
import com.devscore.digital_pharmacy.business.interactors.inventory.local.AddFailureMedicineInteractor
import com.devscore.digital_pharmacy.business.interactors.inventory.local.AddMedicineInteractor
import com.devscore.digital_pharmacy.presentation.session.SessionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

@HiltWorker
class UploadWorker @AssistedInject
constructor(
    @Assisted
    context: Context,
    @Assisted
    parameters: WorkerParameters,
    private val database : AppDatabase,
    private var addMedicineInteractor: AddMedicineInteractor,
    private val interactor : AddFailureMedicineInteractor,
    var sessionManager : SessionManager,
    val service : InventoryApiService,
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
        val localMedicines = database.getLocalMedicineDao().getSyncData().map {
            it.toLocalMedicine()
        }



        Log.d(TAG, localMedicines.size.toString())
        Log.d(TAG, localMedicines.toString())
        Log.d(TAG, addMedicineInteractor.toString())

        Log.d(TAG, "SessionManager " + sessionManager.toString())
        Log.d(TAG, "Token " + sessionManager.state.value?.authToken?.token.toString())


        var call : Boolean = true
        if (localMedicines.size > 0) {
            CoroutineScope(IO).launch {
                Log.d(TAG, Thread.currentThread().name.toString())
                appDataStoreManager.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)?.let { email ->
                    var authToken: AuthToken? = null
                    val entity = accountDao.searchByEmail(email)
                    if(entity != null){
                        authToken = authTokenDao.searchByPk(entity.pk)?.toAuthToken()
                        if(authToken != null && call){
                            call = false
                            Log.d(TAG, "UploadWorker Call")

                            for (addMedicine in localMedicines) {
                                try {
                                    database.getLocalMedicineDao().deleteFailureLocalMedicine(addMedicine.room_medicine_id!!)
                                }
                                catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            interactor.execute(
                                authToken = authToken,
                                medicines = localMedicines,
                                null
                            ).launchIn(this)
                        }
                    }
                }


            }
        }


        val checks = database.getLocalMedicineDao().getSyncData().map {
            it.toLocalMedicine()
        }








        Log.d(TAG, "Size " + checks.size + checks.toString())



    }
}