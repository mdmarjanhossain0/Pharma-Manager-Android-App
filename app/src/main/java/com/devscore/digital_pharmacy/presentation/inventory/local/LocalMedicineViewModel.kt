package com.devscore.digital_pharmacy.presentation.inventory.local

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.devscore.digital_pharmacy.business.domain.util.ErrorHandling
import com.devscore.digital_pharmacy.business.domain.util.StateMessage
import com.devscore.digital_pharmacy.business.domain.util.UIComponentType
import com.devscore.digital_pharmacy.business.domain.util.doesMessageAlreadyExistInQueue
import com.devscore.digital_pharmacy.business.interactors.inventory.local.DeleteLocalMedicine
import com.devscore.digital_pharmacy.business.interactors.inventory.local.SearchLocalMedicine
import com.devscore.digital_pharmacy.business.interactors.shortlist.CreateShortList
import com.devscore.digital_pharmacy.presentation.inventory.global.GlobalEvents
import com.devscore.digital_pharmacy.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LocalMedicineViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val searchLocalMedicine: SearchLocalMedicine,
    private val createShortList: CreateShortList,
    private val deleteLocalMedicine: DeleteLocalMedicine
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<LocalMedicineState> = MutableLiveData(LocalMedicineState())



    private lateinit var callback: LocalMedicineCallback
    fun submit(callback: LocalMedicineCallback) {
        this.callback = callback
    }

    init {
        onTriggerEvent(LocalMedicineEvents.NewLocalMedicineSearch)
    }

    fun onTriggerEvent(event: LocalMedicineEvents) {
        when (event) {
            is LocalMedicineEvents.NewLocalMedicineSearch -> {
                search()
            }

            is LocalMedicineEvents.SearchWithQuery -> {
                searchWithQuery(event.query)
            }
            is LocalMedicineEvents.NextPage -> {
                incrementPageNumber()
                search()
            }

            is LocalMedicineEvents.SetSearchSelection -> {
                selectQuery(event.action)
            }

            is LocalMedicineEvents.UpdateQuery -> {
                onUpdateQuery(event.query)
                clearList()
                resetPage()
            }


            is LocalMedicineEvents.AddShortList -> {
                addShortList(event.medicine)
            }


            is LocalMedicineEvents.DeleteLocalMedicine -> {
                deleteMedicine(event.localMedicine)
            }

            is LocalMedicineEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is LocalMedicineEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun deleteMedicine(localMedicine : LocalMedicine) {
        state.value?.let { state ->
            deleteLocalMedicine.execute(
                authToken = sessionManager.state.value?.authToken,
                localMedicine = localMedicine
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { delete ->
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

    private fun addShortList(medicine: LocalMedicine) {
        state.value?.let { state ->
            createShortList.execute(
                authToken = sessionManager.state.value?.authToken,
                medicine_id = medicine.id!!
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { shortList ->
                    Log.d(TAG, "ViewModel List Size " + shortList)
                    this.state.value = state.copy(shortList = shortList)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun searchWithQuery(query: String) {
        state.value?.let { state ->
            searchLocalMedicine.execute(
                authToken = sessionManager.state.value?.authToken,
                query = query,
                action = state.action,
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



    private fun selectQuery(action: String) {
        state.value.let { state ->
            this.state.value = state?.copy(
                action = action
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
//        resetPage()
//        clearList()


        Log.d(TAG, "ViewModel page number " + state.value?.page)
        state.value?.let { state ->
            searchLocalMedicine.execute(
                authToken = sessionManager.state.value?.authToken,
                query = state.query,
                action = state.action,
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

}


interface LocalMedicineCallback {
    fun onDeleteDone()
}