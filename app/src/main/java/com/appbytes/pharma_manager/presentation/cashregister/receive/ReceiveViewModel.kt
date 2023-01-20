package com.appbytes.pharma_manager.presentation.cashregister.receive

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.StateMessage
import com.appbytes.pharma_manager.business.domain.util.UIComponentType
import com.appbytes.pharma_manager.business.domain.util.doesMessageAlreadyExistInQueue
import com.appbytes.pharma_manager.business.interactors.cashregister.CreateReceiveInteractor
import com.appbytes.pharma_manager.presentation.session.SessionManager
import com.appbytes.pharma_manager.presentation.util.ReceivePaymentType.Companion.CUSTOMER_PAY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ReceiveViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val createReceiveInteractor: CreateReceiveInteractor
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<ReceiveState> = MutableLiveData(ReceiveState())

    init {
    }

    fun onTriggerEvent(event: ReceiveEvents) {
        when (event) {
            is ReceiveEvents.NewReceiveCreate -> {
                createReceive()
            }

            is ReceiveEvents.CacheState -> {
                cacheState(event.receive)
            }

            is ReceiveEvents.AddCustomer -> {
                addCustomer(event.customer)
            }


            is ReceiveEvents.AddSupplier -> {
                addSupplier(event.supplier)
            }

            is ReceiveEvents.AddType -> {
                addType(event.type)
            }

            is ReceiveEvents.AddAmount -> {
                addAmount(event.amount)
            }

            is ReceiveEvents.AddBalance -> {
                addBalance(event.balance)
            }

            is ReceiveEvents.AddRemark -> {
                addRemark(event.remark)
            }

            is ReceiveEvents.AddDate -> {
                addDate(event.date)
            }

            is ReceiveEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is ReceiveEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun addDate(date: String) {
        state.value?.let { state ->
            this.state.value = state.copy(date = date)
        }
    }

    private fun addRemark(remark: String) {
        state.value?.let { state ->
            this.state.value = state.copy(remark = remark)
        }
    }

    private fun addBalance(balance: Float?) {
        state.value?.let { state ->
            this.state.value = state.copy(balance = balance!!)
        }
    }

    private fun addAmount(amount: Float) {
        state.value?.let { state ->
            this.state.value = state.copy(amount = amount)
        }
    }

    private fun addType(type: String) {
        state.value?.let { state ->
            this.state.value = state.copy(type = type)
        }
    }

    private fun addSupplier(supplier: Supplier) {
        state.value?.let { state ->
            this.state.value = state.copy(supplier = supplier)
        }
    }

    private fun addCustomer(customer: Customer?) {
        state.value?.let { state ->
            this.state.value = state.copy(customer = customer)
        }
    }

    private fun cacheState(receive : Receive) {
        state.value?.let { state ->
            this.state.value = state.copy(receive = receive)
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

    private fun createReceive() {
        generateReceive()
        state.value?.let { state ->
            createReceiveInteractor.execute(
                authToken = sessionManager.state.value?.authToken,
                createReceive = state.receive.toCreateReceive()
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { receive ->
                    this.state.value = state.copy(receive = receive)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun generateReceive() {
        state.value?.let { state ->
            var receive : Receive? = null
            if (state.type == CUSTOMER_PAY) {
                receive = Receive(
                    pk = -1,
                    room_id = -1,
                    date = state.date!!,
                    customer = state.customer?.pk,
                    vendor = -1,
                    type = state.type!!,
                    total_amount = state.amount,
                    balance = state.balance,
                    remarks = state.remark,
                    created_at = "",
                    updated_at = "",
                    customer_name = "",
                    vendor_name = ""
                )
            }
            else {
                receive = Receive(
                    pk = -1,
                    room_id = -1,
                    date = state.date!!,
                    customer = -1,
                    vendor = -1,
                    type = state.type!!,
                    total_amount = state.amount,
                    balance = state.balance,
                    remarks = state.remark,
                    created_at = "",
                    updated_at = "",
                    customer_name = "",
                    vendor_name = ""
                )
            }
            Log.d(TAG, "Receive " + receive)
            this.state.value = state.copy(receive = receive)
        }
    }

}