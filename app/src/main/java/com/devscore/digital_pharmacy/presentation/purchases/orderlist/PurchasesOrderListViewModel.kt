package com.devscore.digital_pharmacy.presentation.purchases.orderlist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devscore.digital_pharmacy.business.domain.models.PurchasesOrder
import com.devscore.digital_pharmacy.business.domain.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.devscore.digital_pharmacy.business.domain.util.ErrorHandling
import com.devscore.digital_pharmacy.business.domain.util.StateMessage
import com.devscore.digital_pharmacy.business.domain.util.UIComponentType
import com.devscore.digital_pharmacy.business.domain.util.doesMessageAlreadyExistInQueue
import com.devscore.digital_pharmacy.business.interactors.purchases.DeletePurchasesOrderInteractor
import com.devscore.digital_pharmacy.business.interactors.purchases.PurchasesCompleted
import com.devscore.digital_pharmacy.business.interactors.purchases.SearchPurchasesOrder
import com.devscore.digital_pharmacy.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PurchasesOrderListViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val searchPurchasesOrder: SearchPurchasesOrder,
    private val purchasesCompleted: PurchasesCompleted,
    private val deleteOrder : DeletePurchasesOrderInteractor
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<PurchasesOrderListState> = MutableLiveData(PurchasesOrderListState())


    lateinit var searchJob : Job

    init {
        onTriggerEvent(PurchasesOrderListEvents.SearchNewOrder)
    }

    fun onTriggerEvent(event: PurchasesOrderListEvents) {
        when (event) {
            is PurchasesOrderListEvents.SearchNewOrder -> {
                search()
            }

            is PurchasesOrderListEvents.SearchWithQuery -> {
            }
            is PurchasesOrderListEvents.NextPage -> {
                incrementPageNumber()
                search()
            }

            is PurchasesOrderListEvents.PurchasesCompleted -> {
//                orderCompleted(event.order)
            }

            is PurchasesOrderListEvents.UpdateQuery -> {
                onUpdateQuery(event.query)
            }



            is PurchasesOrderListEvents.DeleteOrder -> {
                deleteOrder(event.order)
            }

            is PurchasesOrderListEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is PurchasesOrderListEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun deleteOrder(order: PurchasesOrder) {
        state.value?.let { state ->
            deleteOrder.execute(
                authToken = sessionManager.state.value?.authToken,
                order = order
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { response ->
                    withContext(Dispatchers.Main) {
                        search()
                    }
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }

    /*private fun orderCompleted(order : PurchasesOrder) {
        state.value?.let { state ->
            purchasesCompleted.execute(
                authToken = sessionManager.state.value?.authToken,
                pk = order.pk!!,
                query = state.query,
                status = 0,
                page = state.page,
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    Log.d(TAG, "ViewModel List Size " + list.size)
                    this.state.value = state.copy(orderList = list)
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
    }*/


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


        Log.d(TAG, "ViewModel page number " + state.value?.page)
        state.value?.let { state ->
            searchJob = searchPurchasesOrder.execute(
                authToken = sessionManager.state.value?.authToken,
                query = state.query,
                status = 0,
                page = state.page,
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    Log.d(TAG, "ViewModel List Size " + list.size)
                    this.state.value = state.copy(orderList = list)
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