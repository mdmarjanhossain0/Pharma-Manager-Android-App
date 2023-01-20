package com.appbytes.pharma_manager.presentation.sales.payment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.*
import com.appbytes.pharma_manager.business.interactors.sales.SalesCompleted
import com.appbytes.pharma_manager.business.interactors.sales.SalesOrderLocalDetailsInteractor
import com.appbytes.pharma_manager.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SalesPayViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val orderLocalDetailsInteractor: SalesOrderLocalDetailsInteractor,
    private val salesCompleted: SalesCompleted,
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<SalesPayState> = MutableLiveData(SalesPayState())
    private lateinit var callback : OnCompleteCallback
    fun submit(callback: OnCompleteCallback) {
        this.callback = callback
    }

    init {
    }

    fun onTriggerEvent(event: SalesPayEvents) {
        when (event) {
            is SalesPayEvents.OrderCompleted -> {
                orderCompleted()
            }

            is SalesPayEvents.OrderDetails -> {
                getOrderDetails(event.pk)
            }

            is SalesPayEvents.ReceiveAmount -> {
                receiveAmount(event.amount!!)
            }

            is SalesPayEvents.IsDiscountPercent -> {
                isDiscountPercent(event.isDiscountPercent)
            }

            is SalesPayEvents.Discount -> {
                discount(event.discount)
            }

            is SalesPayEvents.DeleteMedicine -> {
//                deleteFromCart(event.medicine)
            }


            is SalesPayEvents.SelectCustomer -> {
                selectCustomer(event.customer)
            }

            is SalesPayEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is SalesPayEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }


    private fun orderCompleted() {
        processOder()
        state.value?.let { state ->
            salesCompleted.execute(
                authToken = sessionManager.state.value?.authToken,
                pk = state.order?.pk!!,
                createSalesOder = state.order.toCreateSalesOrder()
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { order ->
                    this.state.value = state.copy(
                        order = order
                    )
                    callback.done()
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun selectCustomer(customer: Customer) {
        state.value?.let { state ->
            this.state.value = state.copy(
                customer = customer
            )
        }
    }


    private fun discount(discount: Float?) {
        state.value?.let {state ->
            var discountAmount = 0f
            if (state.is_discount_percent) {
                discountAmount = ((state.totalAmount!! * discount!!) / 100)
            }
            else {
                discountAmount = discount!!
            }
            val totalAmountAfterDiscount = state.totalAmount!! - discountAmount
            this.state.value = state.copy(
                discount = discount,
                discountAmount = discountAmount,
                totalAmountAfterDiscount = totalAmountAfterDiscount
            )
        }
    }

    private fun isDiscountPercent(discountPercent: Boolean) {
        state.value?.let {state ->
            var discountAmount : Float = 0f
            if (discountPercent) {
                discountAmount = ((state.totalAmount!! * state.discount!!) / 100 )
            }
            else {
                discountAmount = state.discount!!
            }
            val totalAmountAfterDiscount = state.totalAmount!! - discountAmount
            this.state.value = state.copy(
                is_discount_percent = discountPercent,
                discountAmount = discountAmount,
                totalAmountAfterDiscount = totalAmountAfterDiscount
            )
        }
    }

    private fun receiveAmount(amount : Float) {
        state.value?.let {state ->
            this.state.value = state.copy(
                receivedAmount = amount
            )
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


    private fun getOrderDetails(pk : Int) {
        state.value?.let { state ->
            orderLocalDetailsInteractor.execute(
                authToken = sessionManager.state.value?.authToken,
                pk = pk
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { order ->
                    this.state.value = state.copy(
                        order = order,
                        pk = pk,
                        totalAmount = order.total_amount,
                        totalAmountAfterDiscount = order.total_after_discount,
                        discount = order.discount,
                        is_discount_percent = order.is_discount_percent
                    )

                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun processOder() {
        var order : SalesOrder = state.value?.order!!
        state.value?.let { state ->
            order = order.copy(
                    customer = state.customer?.pk,
                    paid_amount = state.receivedAmount,
                    discount = state.discount,
                    total_after_discount = state.totalAmountAfterDiscount,
                    is_discount_percent = state.is_discount_percent
                )
            this.state.value = state.copy(
                order = order
            )
        }
//        return order
    }
}

interface OnCompleteCallback {
    fun done()
}