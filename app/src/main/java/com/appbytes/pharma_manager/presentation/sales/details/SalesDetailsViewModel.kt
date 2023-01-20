package com.appbytes.pharma_manager.presentation.sales.details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.*
import com.appbytes.pharma_manager.business.interactors.account.GetAccount
import com.appbytes.pharma_manager.business.interactors.sales.DeleteSalesOrderInteractor
import com.appbytes.pharma_manager.business.interactors.sales.SalesOrderLocalDetailsInteractor
import com.appbytes.pharma_manager.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SalesDetailsViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val salesOrderLocalDetailsInteractor: SalesOrderLocalDetailsInteractor,
    private val getAccountInteractor: GetAccount,
    private val deleteSalesOrderInteractor: DeleteSalesOrderInteractor
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<SalesDetailsState> = MutableLiveData(SalesDetailsState())
    private lateinit var callback : OnCompleteCallback
    fun submit(callback: OnCompleteCallback) {
        this.callback = callback
    }

    init {
    }

    fun onTriggerEvent(event: SalesDetailsEvents) {
        when (event) {

            is SalesDetailsEvents.OrderDetails -> {
                getOrderDetails(event.pk)
                getAccount()
            }

            is SalesDetailsEvents.DeleteOrder -> {
                deleteOrder(state.value?.order!!)
            }


            is SalesDetailsEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is SalesDetailsEvents.OnRemoveHeadFromQueue -> {
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
            salesOrderLocalDetailsInteractor.execute(
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

    private fun deleteOrder(order : SalesOrder) {
        state.value?.let { state ->
            deleteSalesOrderInteractor.execute(
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