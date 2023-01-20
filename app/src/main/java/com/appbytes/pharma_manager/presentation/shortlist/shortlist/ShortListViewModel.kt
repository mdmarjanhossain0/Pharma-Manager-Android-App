package com.appbytes.pharma_manager.presentation.shortlist.shortlist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appbytes.pharma_manager.business.domain.models.ShortList
import com.appbytes.pharma_manager.business.domain.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.appbytes.pharma_manager.business.domain.util.ErrorHandling
import com.appbytes.pharma_manager.business.domain.util.StateMessage
import com.appbytes.pharma_manager.business.domain.util.UIComponentType
import com.appbytes.pharma_manager.business.domain.util.doesMessageAlreadyExistInQueue
import com.appbytes.pharma_manager.business.interactors.inventory.local.DeleteShortListInteractor
import com.appbytes.pharma_manager.business.interactors.shortlist.SearchShortList
import com.appbytes.pharma_manager.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ShortListViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val searchShortList : SearchShortList,
    private val deleteShortListInteractor : DeleteShortListInteractor
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<ShortListState> = MutableLiveData(ShortListState())

    init {
        onTriggerEvent(ShortListEvents.NewShortListSearch)
    }

    fun onTriggerEvent(event: ShortListEvents) {
        when (event) {
            is ShortListEvents.NewShortListSearch -> {
                search()
            }

            is ShortListEvents.NextPage -> {
                incrementPageNumber()
                search()
            }


            is ShortListEvents.DeleteShortList -> {
                delete(event.shortList)
            }

            is ShortListEvents.UpdateQuery -> {
                onUpdateQuery(event.query)
                clearList()
                resetPage()
            }


            is ShortListEvents.UpdateFilter -> {
                event(event.filter)
                clearList()
                resetPage()
                search()
            }

            is ShortListEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is ShortListEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun event(filter: String) {
        state.value = state.value?.copy(filter = filter)
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
            this.state.value = state.copy(localMedicineList = listOf())
        }
    }

    private fun resetPage() {
        state.value = state.value?.copy(page = 1)
        onUpdateQueryExhausted(false)
    }

    private fun incrementPageNumber() {
        state.value?.let { state ->
            val pageNumber : Int = (state.localMedicineList.size / PAGINATION_PAGE_SIZE) as Int + 1
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
        Log.d(TAG, "Filter " + state.value?.filter)
        state.value?.let { state ->
            searchShortList.execute(
                authToken = sessionManager.state.value?.authToken,
                query = state.query,
                filter = state.filter,
                page = state.page,
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    Log.d(TAG, "ViewModel List Size " + list.size)
                    this.state.value = state.copy(localMedicineList = list)
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



    private fun delete(shortList : ShortList) {

        state.value?.let { state ->
            deleteShortListInteractor.execute(
                authToken = sessionManager.state.value?.authToken,
                pk = shortList.pk!!
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { response ->
                    search()
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }

}