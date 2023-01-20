package com.appbytes.pharma_manager.presentation.main.report

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appbytes.pharma_manager.business.domain.models.Report
import com.appbytes.pharma_manager.business.domain.util.ErrorHandling
import com.appbytes.pharma_manager.business.domain.util.StateMessage
import com.appbytes.pharma_manager.business.domain.util.UIComponentType
import com.appbytes.pharma_manager.business.domain.util.doesMessageAlreadyExistInQueue
import com.appbytes.pharma_manager.business.interactors.report.SearchReport
import com.appbytes.pharma_manager.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ReportViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val searchReport: SearchReport
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<ReportState> = MutableLiveData(ReportState())

    init {
        onTriggerEvent(ReportEvents.NewReportSearch)
    }

    fun onTriggerEvent(event: ReportEvents) {
        when (event) {
            is ReportEvents.NewReportSearch -> {
                search()
            }

            is ReportEvents.NextPage -> {
                incrementPageNumber()
                search()
            }

            is ReportEvents.UpdateQuery -> {
                onUpdateQuery(event.query)
                clearList()
                resetPage()
                search()
            }

            is ReportEvents.UpdateStart -> {
                onUpdateStart(event.start)
                clearList()
                resetPage()
                search()
            }

            is ReportEvents.UpdateEnd -> {
                onUpdateEnd(event.end)
                clearList()
                resetPage()
                search()
            }

            is ReportEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is ReportEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun onUpdateStart(start: String) {
        state.value?.let { state ->
            this.state.value = state.copy(start = start)
        }
    }

    private fun onUpdateEnd(end: String) {
        state.value?.let { state ->
            this.state.value = state.copy(end = end)
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
            this.state.value = state.copy(reportList = listOf())
        }
    }

    private fun resetPage() {
        state.value = state.value?.copy(page = 1)
        onUpdateQueryExhausted(false)
    }

    private fun incrementPageNumber() {
        state.value?.let { state ->
            val pageNumber : Int = (state.reportList.size / 20) as Int + 1
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
            searchReport.execute(
                authToken = sessionManager.state.value?.authToken,
                query = state.query,
                start = state.start,
                end = state.end,
                page = state.page,
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    Log.d(TAG, "ViewModel List Size " + list.size)
                    val previousList = state.reportList
                    this.state.value = state.copy(
                        isLoading = false,
                        reportList = addList(previousList, list)
                    )
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









    fun addList(previousList : List<Report>, newList: List<Report>) : List<Report> {
        val list = previousList.toMutableList()
        for (item in newList) {
            val find = previousList.find { it.pk == item.pk && it.type.equals(item.type) }
            if (find == null) {
                list.add(item)
            }
        }
        return list
    }
}