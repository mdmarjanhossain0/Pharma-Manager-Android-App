package com.devscore.digital_pharmacy.presentation.purchases.details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devscore.digital_pharmacy.business.domain.models.PurchasesOrder
import com.devscore.digital_pharmacy.business.domain.models.SalesOrder
import com.devscore.digital_pharmacy.business.domain.util.StateMessage
import com.devscore.digital_pharmacy.business.domain.util.UIComponentType
import com.devscore.digital_pharmacy.business.domain.util.doesMessageAlreadyExistInQueue
import com.devscore.digital_pharmacy.business.interactors.account.GetAccount
import com.devscore.digital_pharmacy.business.interactors.purchases.DeletePurchasesOrderInteractor
import com.devscore.digital_pharmacy.business.interactors.purchases.PurchasesOrderLocalDetailsInteractor
import com.devscore.digital_pharmacy.business.interactors.sales.DeleteSalesOrderInteractor
import com.devscore.digital_pharmacy.business.interactors.sales.SalesOrderLocalDetailsInteractor
import com.devscore.digital_pharmacy.presentation.sales.details.SalesDetailsEvents
import com.devscore.digital_pharmacy.presentation.sales.details.SalesDetailsState
import com.devscore.digital_pharmacy.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val orderLocalDetailsInteractor: PurchasesOrderLocalDetailsInteractor,
    private val getAccountInteractor: GetAccount,
    private val deletePurchasesOrderInteractor: DeletePurchasesOrderInteractor
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<OrderDetailsState> = MutableLiveData(OrderDetailsState())
    private lateinit var callback : OnCompleteCallback
    fun submit(callback: OnCompleteCallback) {
        this.callback = callback
    }

    init {
    }

    fun onTriggerEvent(event: OrderDetailsEvents) {
        when (event) {

            is OrderDetailsEvents.OrderDetails -> {
                getOrderDetails(event.pk)
                getAccount()
            }

            is OrderDetailsEvents.DeleteOrder -> {
                deleteOrder(state.value?.order!!)
            }


            is OrderDetailsEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is OrderDetailsEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun getAccount() {
        state.value?.let { state ->
            getAccountInteractor.execute(
                authToken = sessionManager.state.value?.authToken
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
//                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { account ->
                    this.state.value = state.copy(
                        account = account,
                        isLoading = false
                    )
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun getOrderDetails(pk : Int) {
        state.value?.let { state ->
            this.state.value = state.copy(
                pk = pk
            )
        }

        state.value?.let { state ->
            orderLocalDetailsInteractor.execute(
                authToken = sessionManager.state.value?.authToken,
                pk = pk
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { order ->
                    this.state.value = state.copy(
                        order = order)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
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

    private fun deleteOrder(order : PurchasesOrder) {
        state.value?.let { state ->
            deletePurchasesOrderInteractor.execute(
                authToken = sessionManager.state.value?.authToken,
                order = order
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { response ->
                    if (response.response == "Successfully Deleted") {
                        callback.delete()




//                        withContext(Dispatchers.Main){
//                            search()
//                        }
                    }
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }
}


interface OnCompleteCallback {
    fun delete()
}