package com.appbytes.pharma_manager.presentation.cashregister.payment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.StateMessage
import com.appbytes.pharma_manager.business.domain.util.UIComponentType
import com.appbytes.pharma_manager.business.domain.util.doesMessageAlreadyExistInQueue
import com.appbytes.pharma_manager.business.interactors.cashregister.CreatePaymentInteractor
import com.appbytes.pharma_manager.presentation.session.SessionManager
import com.appbytes.pharma_manager.presentation.util.ReceivePaymentType.Companion.VENDOR_PAY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val createPaymentInteractor: CreatePaymentInteractor
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<PaymentState> = MutableLiveData(PaymentState())

    init {
    }

    fun onTriggerEvent(event: PaymentEvents) {
        when (event) {
            is PaymentEvents.NewPaymentCreate -> {
                createPayment()
            }

            is PaymentEvents.CacheState -> {
                cacheState(event.payment)
            }

            is PaymentEvents.AddType -> {
                addType(event.type)
            }

            is PaymentEvents.AddSupplier -> {
                addSupplier(event.supplier)
            }

            is PaymentEvents.AddAmount -> {
                addAmount(event.amount)
            }

            is PaymentEvents.AddBalance -> {
                addBalance(event.balance)
            }

            is PaymentEvents.AddRemark -> {
                addRemark(event.remark)
            }

            is PaymentEvents.AddDate -> {
                addDate(event.date)
            }

            is PaymentEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is PaymentEvents.OnRemoveHeadFromQueue -> {
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

    private fun addBalance(balance: Float) {
        state.value?.let { state ->
            this.state.value = state.copy(balance = balance)
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


    private fun addSupplier(supplier: Supplier?) {
        state.value?.let { state ->
            this.state.value = state.copy(supplier = supplier)
        }
    }

    private fun addCustomer(customer: Customer) {
        state.value?.let { state ->
            this.state.value = state.copy(customer = customer)
        }
    }


    private fun cacheState(payment : Payment) {
        state.value?.let { state ->
            this.state.value = state.copy(payment = payment)
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

    private fun createPayment() {
        generatePayment()
        state.value?.let { state ->
            createPaymentInteractor.execute(
                authToken = sessionManager.state.value?.authToken,
                createPayment = state.payment.toCreatePayment()
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { payment ->
                    this.state.value = state.copy(payment = payment)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun generatePayment() {
        state.value?.let { state ->
            var payment : Payment? = null
            if (state.type == VENDOR_PAY) {
                payment = Payment(
                    pk = -1,
                    room_id = -1,
                    date = state.date!!,
                    customer = -1,
                    vendor = state.supplier?.pk,
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
                payment = Payment(
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
            this.state.value = state.copy(payment = payment)
        }
    }

}