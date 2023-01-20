package com.appbytes.pharma_manager.presentation.purchases.ordercompleted

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appbytes.pharma_manager.business.domain.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.appbytes.pharma_manager.business.domain.util.ErrorHandling
import com.appbytes.pharma_manager.business.domain.util.StateMessage
import com.appbytes.pharma_manager.business.domain.util.UIComponentType
import com.appbytes.pharma_manager.business.domain.util.doesMessageAlreadyExistInQueue
import com.appbytes.pharma_manager.business.interactors.purchases.SearchPurchasesOrder
import com.appbytes.pharma_manager.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PurchasesOrderCompletedViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val searchPurchasesOrder: SearchPurchasesOrder
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<PurchasesOrderCompletedState> = MutableLiveData(PurchasesOrderCompletedState())

    init {
        onTriggerEvent(PurchasesOrderCompletedEvents.SearchNewOrder)
    }

    fun onTriggerEvent(event: PurchasesOrderCompletedEvents) {
        when (event) {
            is PurchasesOrderCompletedEvents.SearchNewOrder -> {
                search()
            }

            is PurchasesOrderCompletedEvents.SearchWithQuery -> {
            }
            is PurchasesOrderCompletedEvents.NextPage -> {
                incrementPageNumber()
                search()
            }

            is PurchasesOrderCompletedEvents.UpdateQuery -> {
                onUpdateQuery(event.query)
                clearList()
                resetPage()
            }

            is PurchasesOrderCompletedEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is PurchasesOrderCompletedEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
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
//        state.value?.let { state ->
//            this.state.value = state.copy(page = state.page + 1)
//        }
    }

    private fun onUpdateQuery(query: String) {
        state.value = state.value?.copy(query = query)
    }


    private fun search() {
//        resetPage()
//        clearList()


        Log.d(TAG, "ViewModel page number " + state.value?.page)
        state.value?.let { state ->
            searchPurchasesOrder.execute(
                authToken = sessionManager.state.value?.authToken,
                query = state.query,
                status = 3,
                page = state.page,
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    Log.d(TAG, "ViewModel List Size " + list.size)
                    this.state.value = state.copy(
                        orderList = list,
                        isLoading = false
                    )
                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message?.contains(ErrorHandling.INVALID_PAGE) == true){
                        onUpdateQueryExhausted(true)
                    }else{
                        appendToMessageQueue(stateMessage)
                    }
                    this.state.value = state.copy(isLoading = dataState.isLoading)
                }

            }.launchIn(viewModelScope)
        }
    }

}