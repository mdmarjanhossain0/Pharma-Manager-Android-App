package com.appbytes.pharma_manager.presentation.main.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appbytes.pharma_manager.business.datasource.cache.AppDatabase
import com.appbytes.pharma_manager.business.datasource.cache.cashregister.FailurePaymentEntity
import com.appbytes.pharma_manager.business.datasource.cache.cashregister.FailureReceiveEntity
import com.appbytes.pharma_manager.business.datasource.cache.cashregister.PaymentDao
import com.appbytes.pharma_manager.business.datasource.cache.cashregister.ReceiveDao
import com.appbytes.pharma_manager.business.datasource.cache.customer.CustomerDao
import com.appbytes.pharma_manager.business.datasource.cache.customer.FailureCustomerEntity
import com.appbytes.pharma_manager.business.datasource.cache.customer.toCustomer
import com.appbytes.pharma_manager.business.datasource.cache.inventory.local.FailureMedicineWithUnit
import com.appbytes.pharma_manager.business.datasource.cache.inventory.local.LocalMedicineDao
import com.appbytes.pharma_manager.business.datasource.cache.inventory.local.toLocalMedicine
import com.appbytes.pharma_manager.business.datasource.cache.purchases.FailurePurchasesOrderWithMedicine
import com.appbytes.pharma_manager.business.datasource.cache.purchases.PurchasesDao
import com.appbytes.pharma_manager.business.datasource.cache.purchases.toPurchasesOrder
import com.appbytes.pharma_manager.business.datasource.cache.sales.FailureSalesOrderWithMedicine
import com.appbytes.pharma_manager.business.datasource.cache.sales.SalesDao
import com.appbytes.pharma_manager.business.datasource.cache.sales.toSalesOder
import com.appbytes.pharma_manager.business.datasource.cache.shortlist.FailureShortListEntity
import com.appbytes.pharma_manager.business.datasource.cache.shortlist.ShortListDao
import com.appbytes.pharma_manager.business.datasource.cache.supplier.FailureSupplierEntity
import com.appbytes.pharma_manager.business.datasource.cache.supplier.SupplierDao
import com.appbytes.pharma_manager.business.datasource.cache.supplier.toSupplier
import com.appbytes.pharma_manager.business.domain.util.ErrorHandling
import com.appbytes.pharma_manager.business.domain.util.StateMessage
import com.appbytes.pharma_manager.business.domain.util.UIComponentType
import com.appbytes.pharma_manager.business.domain.util.doesMessageAlreadyExistInQueue
import com.appbytes.pharma_manager.business.interactors.cashregister.CreateFailurePaymentInteractor
import com.appbytes.pharma_manager.business.interactors.cashregister.CreateFailureReceiveInteractor
import com.appbytes.pharma_manager.business.interactors.customer.CreateFailureCustomerInteractor
import com.appbytes.pharma_manager.business.interactors.inventory.local.AddFailureMedicineInteractor
import com.appbytes.pharma_manager.business.interactors.purchases.CreateFailurePurchasesOrderInteractor
import com.appbytes.pharma_manager.business.interactors.report.GetMonthDetailsSales
import com.appbytes.pharma_manager.business.interactors.sales.CreateFailureSalesInteractor
import com.appbytes.pharma_manager.business.interactors.shortlist.CreateFailureShortListInteractor
import com.appbytes.pharma_manager.business.interactors.supplier.CreateFailureSuppllierInteractor
import com.appbytes.pharma_manager.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val salesDetailsMonth : GetMonthDetailsSales,


    private val database : AppDatabase,
    private var medicineInteractor : AddFailureMedicineInteractor,
    private var salesInteractor  : CreateFailureSalesInteractor,
    private var purchasesInteractor : CreateFailurePurchasesOrderInteractor,
    private var customerInteractor : CreateFailureCustomerInteractor,
    private var supplierInteractor : CreateFailureSuppllierInteractor,
    private var shortListInteractor : CreateFailureShortListInteractor,
    private val receiveInteractor : CreateFailureReceiveInteractor,
    private val paymentInteractor : CreateFailurePaymentInteractor,




    private val localMedicineDao: LocalMedicineDao,
    private val salesDao : SalesDao,
    private val purchasesDao: PurchasesDao,
    private val customerDao : CustomerDao,
    private val supplierDao: SupplierDao,
    private val shortListDao : ShortListDao,
    private val receiveDao : ReceiveDao,
    private val paymentDao : PaymentDao
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<DashBoardState> = MutableLiveData(DashBoardState())
    lateinit var localMedicineLiveData: LiveData<List<FailureMedicineWithUnit>>
    lateinit var salesLiveDataLiveData : LiveData<List<FailureSalesOrderWithMedicine>>
    lateinit var purchasesLiveData : LiveData<List<FailurePurchasesOrderWithMedicine>>
    lateinit var customerLiveData : LiveData<List<FailureCustomerEntity>>
    lateinit var supplierLiveData : LiveData<List<FailureSupplierEntity>>
    lateinit var shortListLiveData: LiveData<List<FailureShortListEntity>>
    lateinit var receiveLiveData : LiveData<List<FailureReceiveEntity>>
    lateinit var paymentLiveData : LiveData<List<FailurePaymentEntity>>

    init {
        initLiveData()
        onTriggerEvent(DashBoardEvents.GetMonthSalesTotalReport)
    }

    private fun initLiveData() {
        localMedicineLiveData = localMedicineDao.getSyncDataLiveData()
        salesLiveDataLiveData = salesDao.getSyncDataLiveData()
        purchasesLiveData = purchasesDao.getSyncDataLiveData()
        customerLiveData = customerDao.getSyncDataLiveData()
        supplierLiveData = supplierDao.getSyncDataLiveData()
        shortListLiveData = shortListDao.getSyncDataLiveData()
        receiveLiveData = receiveDao.getSyncDataLiveData()
        paymentLiveData = paymentDao.getSyncDataLiveData()
    }

    fun onTriggerEvent(event: DashBoardEvents) {
        when (event) {
            is DashBoardEvents.GetMonthSalesTotalReport -> {
                search()
            }

            is DashBoardEvents.Local -> {
                local(event.value)
            }

            is DashBoardEvents.Sales -> {
                sales(event.value)
            }

            is DashBoardEvents.Purchases -> {
                purchases(event.value)
            }

            is DashBoardEvents.Customer -> {
                customer(event.value)
            }

            is DashBoardEvents.Supplier -> {
                supplier(event.value)
            }

            is DashBoardEvents.Sync -> {
                sync()
            }

            is DashBoardEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is DashBoardEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun dataList() {
        localMedicineLiveData = localMedicineDao.getSyncDataLiveData()
        salesLiveDataLiveData = salesDao.getSyncDataLiveData()
        purchasesLiveData = purchasesDao.getSyncDataLiveData()
        customerLiveData = customerDao.getSyncDataLiveData()
        supplierLiveData = supplierDao.getSyncDataLiveData()
    }

    private fun local(value : Int) {
        state.value?.let { state ->
            this.state.value = state.copy(local = value)
        }
    }

    private fun sales(value : Int) {
        state.value?.let { state ->
            this.state.value = state.copy(sales = value)
        }
    }


    private fun purchases(value : Int) {
        state.value?.let { state ->
            this.state.value = state.copy(purchases = value)
        }
    }

    private fun customer(value : Int) {
        state.value?.let { state ->
            this.state.value = state.copy(customer = value)
        }
    }

    private fun supplier(value : Int) {
        state.value?.let { state ->
            this.state.value = state.copy(supplier = value)
        }
    }


    private fun removeHeadFromQueue() {
        state.value?.let { state ->
            try {
                val queue = state.queue
                queue.remove() // can throw exception if empty
                this.state.value = state.copy(queue = queue)
            } catch (e: Exception) {
                Log.d(TAG, "removeHeadFromQueue: Nothing to remove from DialogQueue")
            }
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        state.value?.let { state ->
            val queue = state.queue
            if(!stateMessage.doesMessageAlreadyExistInQueue(queue = queue)){
                if(!(stateMessage.response.uiComponentType is UIComponentType.None)){
                    queue.add(stateMessage)
                    this.state.value = state.copy(queue = queue)
                }
            }
        }
    }

    private fun onUpdateQueryExhausted(isExhausted: Boolean) {
        state.value?.let { state ->
            this.state.value = state.copy(isQueryExhausted = isExhausted)
        }
    }

//    private fun clearList() {
//        state.value?.let { state ->
//            this.state.value = state.copy(localMedicineList = listOf())
//        }
//    }

    private fun resetPage() {
        state.value = state.value?.copy(page = 1)
        onUpdateQueryExhausted(false)
    }

//    private fun incrementPageNumber() {
//        state.value?.let { state ->
//            val pageNumber : Int = (state.localMedicineList.size / 10) as Int + 1
//            Log.d(TAG, "Pre increment page number " + pageNumber)
//            this.state.value = state.copy(page = pageNumber)
//        }
//        state.value?.let { state ->
//            this.state.value = state.copy(page = state.page + 1)
//        }
//    }

    private fun onUpdateQuery(query: String) {
        state.value = state.value?.copy(query = query)
    }


    private fun search() {
//        resetPage()
//        clearList()


        Log.d(TAG, "ViewModel page number " + state.value?.page)
        state.value?.let { state ->
            salesDetailsMonth.execute(
                authToken = sessionManager.state.value?.authToken
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { details ->
                    this.state.value = state.copy(
                        isLoading = false,
                        details = details)
                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message?.contains(ErrorHandling.INVALID_PAGE) == true){
                        onUpdateQueryExhausted(true)
                    }else{
                        appendToMessageQueue(stateMessage)
                    }
                }

            }.launchIn(viewModelScope)
        }
    }








    private fun sync() {
        Log.d(TAG, "Enter successful")

        var call : Boolean = true
        CoroutineScope(Dispatchers.IO).launch {
            val localMedicines = database.getLocalMedicineDao().getSyncData().map {
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
            val authToken = sessionManager.state.value?.authToken
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




            shortListInteractor.execute(sessionManager.state.value?.authToken).launchIn(viewModelScope)
            receiveInteractor.execute(sessionManager.state.value?.authToken).launchIn(viewModelScope)
            paymentInteractor.execute(sessionManager.state.value?.authToken).launchIn(viewModelScope)
        }
    }
}