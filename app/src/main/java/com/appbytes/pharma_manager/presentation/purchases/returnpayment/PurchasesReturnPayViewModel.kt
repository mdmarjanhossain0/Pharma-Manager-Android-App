package com.appbytes.pharma_manager.presentation.purchases.returnpayment

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.StateMessage
import com.appbytes.pharma_manager.business.domain.util.UIComponentType
import com.appbytes.pharma_manager.business.domain.util.doesMessageAlreadyExistInQueue
import com.appbytes.pharma_manager.business.interactors.purchases.PurchasesOrderLocalDetailsInteractor
import com.appbytes.pharma_manager.business.interactors.purchases.PurchasesReturnInteractor
import com.appbytes.pharma_manager.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PurchasesReturnPayViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val orderLocalDetailsInteractor: PurchasesOrderLocalDetailsInteractor,
    private val purchasesReturnInteractor: PurchasesReturnInteractor
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<PurchasesReturnPayState> = MutableLiveData(PurchasesReturnPayState())
    private lateinit var callback : OnCompleteCallback
    fun submit(callback: OnCompleteCallback) {
        this.callback = callback
    }

    init {
    }

    fun onTriggerEvent(event: PurchasesReturnPayEvents) {
        when (event) {
            is PurchasesReturnPayEvents.OrderCompleted -> {
                createNewOrder()
            }

            is PurchasesReturnPayEvents.OrderDetails -> {
                getOrderDetails(event.pk)
            }

            is PurchasesReturnPayEvents.ReceiveAmount -> {
                receiveAmount(event.amount!!)
            }

            is PurchasesReturnPayEvents.IsDiscountPercent -> {
                isFinePercent(event.isDiscountPercent)
            }

            is PurchasesReturnPayEvents.Discount -> {
                fine(event.discount)
            }

            is PurchasesReturnPayEvents.DeleteMedicine -> {
//                deleteFromCart(event.medicine)
            }


            is PurchasesReturnPayEvents.SelectVendor -> {
                selectCustomer(event.vendor)
            }

            is PurchasesReturnPayEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is PurchasesReturnPayEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }


    private fun createNewOrder() {
        processOder()
        Log.d(TAG, "ViewModel page number " + state.value)
        state.value?.let { state ->
            purchasesReturnInteractor.execute(
                authToken = sessionManager.state.value?.authToken,
                state.returnOrder!!
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { order ->
                    if (order.pk != null) {
                        this.state.value = state.copy(
                            returnOrder = order.toCreatePurchasesReturn()
                        )
                    }

                    this.state.value = state.copy(
                        returnOrder = order.toCreatePurchasesReturn()
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
                returnOrder = CreatePurchasesReturn (
                    vendor = state.vendor?.pk,
                    purchases_order = state.order?.pk,
                    total_amount = state.totalAmount?.toFloat(),
                    total_after_fine = state.totalAmountAfterFine?.toFloat(),
                    return_amount = state.returnAmount!!,
                    fine = state.fineAmount,
                    is_fine_percent = (state.fine == state.totalAmountAfterFine),
                    purchases_return_medicines = list.map { it.toCreatePurchasesOrderMedicine() }
                )
            )
        }
    }

    private fun processOrderMedicine() : List<PurchasesOrderMedicine> {
        val list = mutableListOf<PurchasesOrderMedicine>()
        state.value?.let {state ->
            for (item in state.order?.purchases_order_medicines!!) {
                list.add(
                    item
                )
            }
        }
        return list
    }

    private fun selectCustomer(vendor: Supplier) {
        state.value?.let { state ->
            this.state.value = state.copy(
                vendor = vendor
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