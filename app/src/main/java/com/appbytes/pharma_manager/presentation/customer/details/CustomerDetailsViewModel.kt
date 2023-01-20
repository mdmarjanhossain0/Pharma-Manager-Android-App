package com.appbytes.pharma_manager.presentation.customer.details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appbytes.pharma_manager.business.domain.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.appbytes.pharma_manager.business.domain.util.ErrorHandling
import com.appbytes.pharma_manager.business.domain.util.StateMessage
import com.appbytes.pharma_manager.business.domain.util.UIComponentType
import com.appbytes.pharma_manager.business.domain.util.doesMessageAlreadyExistInQueue
import com.appbytes.pharma_manager.business.interactors.customer.CustomerPreviousOrderInteractor
import com.appbytes.pharma_manager.business.interactors.customer.GetCustomerDetails
import com.appbytes.pharma_manager.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CustomerDetailsViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val customerDetails: GetCustomerDetails,
    private val customerPreviousOrderInteractor: CustomerPreviousOrderInteractor
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<CustomerDetailsState> = MutableLiveData(CustomerDetailsState())


    init {

    }

    fun onTriggerEvent(event: CustomerDetailsEvents) {
        when (event) {
            is CustomerDetailsEvents.SearchOrders -> {
                search(event.pk)
            }

            is CustomerDetailsEvents.GetDetails -> {
                getDetails(event.pk)
//                search(event.pk)
            }

            is CustomerDetailsEvents.NextPage -> {
                incrementPageNumber()
                search(event.pk)
            }

            is CustomerDetailsEvents.UpdateQuery -> {
                onUpdateQuery(event.query)
            }

            is CustomerDetailsEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is CustomerDetailsEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun getDetails(pk: Int) {
        state.value?.let { state ->
            this.state.value = state.copy(
                pk = pk
            )
            customerDetails.execute(
                authToken = sessionManager.state.value?.authToken,
                pk = pk
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
//                this.state.value = state.copy(isLoading = false)

                dataState.data?.let { details ->
                    this.state.value = state.copy(
                        customer = details,
                        pk = pk,
                    )
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

    private fun onUpdateQueryExhausted(isExhausted: Boolean) {
        state.value?.let { state ->
            this.state.value = state.copy(isQueryExhausted = isExhausted)
        }
    }

    private fun clearList() {
        state.value?.let { state ->
            this.state.value = state.copy(orderList = listOf())
        }
    }

    private fun resetPage() {
        state.value = state.value?.copy(page = 1)
        onUpdateQueryExhausted(false)
    }

    private fun incrementPageNumber() {
        state.value?.let { state ->
            val pageNumber : Int = (state.orderList.size / PAGINATION_PAGE_SIZE) as Int + 1
            Log.d(TAG, "Pre increment page number " + pageNumber)
            this.state.value = state.copy(page = pageNumber)
        }
    }

    private fun onUpdateQuery(query: String) {
        state.value = state.value?.copy(query = query)
    }


    private fun search(pk: Int) {

        Log.d(TAG, "ViewModel page number " + state.value?.page)
        Log.d(TAG, "ViewModel pk  " + pk.toString())
        state.value?.let { state ->
            customerPreviousOrderInteractor.execute(
                authToken = sessionManager.state.value?.authToken,
                pk = pk,
                page = state.page
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoadingList = dataState.isLoading)

                dataState.data?.let { list ->
                    Log.d(TAG, "ViewModel List Size " + list.size)
                    this.state.value = state.copy(
                        orderList = list)
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

}