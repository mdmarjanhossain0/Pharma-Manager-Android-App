package com.devscore.digital_pharmacy.presentation.sales.returnpayment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.ErrorHandling
import com.devscore.digital_pharmacy.business.domain.util.StateMessage
import com.devscore.digital_pharmacy.business.domain.util.UIComponentType
import com.devscore.digital_pharmacy.business.domain.util.doesMessageAlreadyExistInQueue
import com.devscore.digital_pharmacy.business.interactors.sales.SalesCompleted
import com.devscore.digital_pharmacy.business.interactors.sales.SalesOrderLocalDetailsInteractor
import com.devscore.digital_pharmacy.business.interactors.sales.SalesReturnInteractor
import com.devscore.digital_pharmacy.presentation.sales.payment.SalesPayEvents
import com.devscore.digital_pharmacy.presentation.sales.payment.SalesPayState
import com.devscore.digital_pharmacy.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SalesReturnPayViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val orderLocalDetailsInteractor: SalesOrderLocalDetailsInteractor,
    private val salesCompleted: SalesCompleted,
    private val createSalesReturnInteractor: SalesReturnInteractor,
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<SalesReturnPayState> = MutableLiveData(SalesReturnPayState())
    private lateinit var callback : OnCompleteCallback
    fun submit(callback: OnCompleteCallback) {
        this.callback = callback
    }

    init {
    }

    fun onTriggerEvent(event: SalesReturnPayEvents) {
        when (event) {
            is SalesReturnPayEvents.OrderCompleted -> {
                createNewOrder()
            }

            is SalesReturnPayEvents.OrderDetails -> {
                getOrderDetails(event.pk)
            }

            is SalesReturnPayEvents.ReceiveAmount -> {
                receiveAmount(event.amount!!)
            }

            is SalesReturnPayEvents.IsDiscountPercent -> {
                isFinePercent(event.isDiscountPercent)
            }

            is SalesReturnPayEvents.Discount -> {
                fine(event.discount)
            }

            is SalesReturnPayEvents.DeleteMedicine -> {
//                deleteFromCart(event.medicine)
            }


            is SalesReturnPayEvents.SelectCustomer -> {
                selectCustomer(event.customer)
            }

            is SalesReturnPayEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is SalesReturnPayEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }


    private fun createNewOrder() {
        processOder()
        Log.d(TAG, "ViewModel page number " + state.value)
        state.value?.let { state ->
            createSalesReturnInteractor.execute(
                authToken = sessionManager.state.value?.authToken,
                state.returnOrder!!
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { order ->
                    if (order.pk != null) {
                        this.state.value = state.copy(
                            returnOrder = order.toCreateSalesReturn()
                        )
                    }

                    this.state.value = state.copy(
                        returnOrder = order.toCreateSalesReturn()
                    )
                    callback.done()
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun processOder() {
        val list = processOrderMedicine()
        state.value?.let { state ->
            this.state.value = state.copy(
                returnOrder = CreateSalesReturn (
                    customer = state.customer?.pk,
                    sales_order = state.order?.pk,
                    total_amount = state.totalAmount?.toFloat(),
                    total_after_fine = state.totalAmountAfterFine?.toFloat(),
                    return_amount = state.returnAmount!!,
                    fine = state.fineAmount,
                    is_fine_percent = (state.fine == state.totalAmountAfterFine),
                    sales_return_medicines = list.map { it.toCreateSalesOrderMedicine() }
                )
            )
        }
    }

    private fun processOrderMedicine() : List<SalesOrderMedicine> {
        val list = mutableListOf<SalesOrderMedicine>()
        state.value?.let {state ->
            for (item in state.order?.sales_oder_medicines!!) {
                list.add(
                    item
                )
            }
        }
        return list
    }

    private fun selectCustomer(customer: Customer) {
        state.value?.let { state ->
            this.state.value = state.copy(
                customer = customer
            )
        }
    }

    private fun fine(discount: Float?) {
        state.value?.let {state ->
            var discountAmount = 0f
            if (state.is_fine_percent) {
                discountAmount = ((state.totalAmount!! * discount!!) / 100)
            }
            else {
                discountAmount = discount!!
            }
            val totalAmountAfterDiscount = state.totalAmount!! + discountAmount
            this.state.value = state.copy(
                fine = discount,
                fineAmount = discountAmount,
                totalAmountAfterFine = totalAmountAfterDiscount
            )
        }
    }

    private fun isFinePercent(discountPercent: Boolean) {
        state.value?.let {state ->
            var discountAmount : Float = 0f
            if (discountPercent) {
                discountAmount = ((state.totalAmount!! * state.fine!!) / 100 )
            }
            else {
                discountAmount = state.fine!!
            }
            val totalAmountAfterDiscount = state.totalAmount!! + discountAmount
            this.state.value = state.copy(
                is_fine_percent = discountPercent,
                fineAmount = discountAmount,
                totalAmountAfterFine = totalAmountAfterDiscount
            )
        }
    }

    private fun receiveAmount(amount : Float) {
        state.value?.let {state ->
            this.state.value = state.copy(
                returnAmount = amount
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
                        totalAmountAfterFine = order.total_after_discount,
                        fine = order.discount,
                        is_fine_percent = order.is_discount_percent
                    )

                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }
}

interface OnCompleteCallback {
    fun done()
}