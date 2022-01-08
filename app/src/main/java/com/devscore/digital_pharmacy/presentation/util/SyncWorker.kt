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
import com.devscore.digital_pharmacy.business.datasource.cache.customer.toCustomer
import com.devscore.digital_pharmacy.business.datasource.cache.inventory.local.toLocalMedicine
import com.devscore.digital_pharmacy.business.datasource.cache.purchases.toPurchasesOrder
import com.devscore.digital_pharmacy.business.datasource.cache.sales.toSalesOder
import com.devscore.digital_pharmacy.business.datasource.cache.supplier.toSupplier
import com.devscore.digital_pharmacy.business.datasource.datastore.AppDataStore
import com.devscore.digital_pharmacy.business.domain.models.AuthToken
import com.devscore.digital_pharmacy.business.interactors.customer.CreateFailureCustomerInteractor
import com.devscore.digital_pharmacy.business.interactors.inventory.local.AddFailureMedicineInteractor
import com.devscore.digital_pharmacy.business.interactors.purchases.CreateFailurePurchasesOrderInteractor
import com.devscore.digital_pharmacy.business.interactors.sales.CreateFailureSalesInteractor
import com.devscore.digital_pharmacy.business.interactors.supplier.CreateFailureSuppllierInteractor
import com.devscore.digital_pharmacy.presentation.session.SessionManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch


@HiltWorker
class SyncWorker @AssistedInject
constructor(
    @Assisted
    context: Context,
    @Assisted
    parameters: WorkerParameters,
    private val database : AppDatabase,
    private val localMedicineDao : AppDatabase,
    private var medicineInteractor : AddFailureMedicineInteractor,
    private var salesInteractor  : CreateFailureSalesInteractor,
    private var purchasesInteractor : CreateFailurePurchasesOrderInteractor,
    private var customerInteractor : CreateFailureCustomerInteractor,
    private var supplierInteractor : CreateFailureSuppllierInteractor,
    var sessionManager : SessionManager,
    val appDataStoreManager : AppDataStore,
    val accountDao: AccountDao,
    val authTokenDao: AuthTokenDao
) : Worker(context, parameters) {

    val TAG = "SyncWorker"

    override fun doWork(): Result {

//        sync()
        return Result.success()
    }

    private fun sync() {
        Log.d(TAG, "Enter successful")
        val localMedicines = localMedicineDao.getLocalMedicineDao().getSyncData().map {
            it.toLocalMedicine()
        }
        val salesOrders = database.getSalesDao().getSyncData().map {
            it.toSalesOder()
        }
        val purchasesOrders = database.getPurchasesDao().getSyncData().map {
            it.toPurchasesOrder()
        }
        val customers = database.getCustomerDao().getSyncData().map {
            it.toCustomer()
        }
        val suppliers = database.getSupplierDao().getSyncData().map {
            it.toSupplier()
        }
        var call : Boolean = true
        CoroutineScope(Dispatchers.IO).launch {
            appDataStoreManager.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)?.let { email ->
                var authToken: AuthToken? = null
                val entity = accountDao.searchByEmail(email)
                if(entity != null){
                    authToken = authTokenDao.searchByPk(entity.pk)?.toAuthToken()
                    if(authToken != null && call){

                        call = false
                        Log.d(TAG, "SyncWorker Call")
                        medicineInteractor.execute(
                            authToken = authToken,
                            medicines = localMedicines,
                            null
                        ).launchIn(this)

                        salesInteractor.execute(
                            authToken,
                            salesOrders
                        ).launchIn(this)




                        purchasesInteractor.execute(
                            authToken,
                            purchasesOrders
                        ).launchIn(this)

                        customerInteractor.execute(
                            authToken,
                            customers
                        ).launchIn(this)

                        supplierInteractor.execute(
                            authToken,
                            suppliers
                        ).launchIn(this)
                    }
                }
            }
        }
    }
}