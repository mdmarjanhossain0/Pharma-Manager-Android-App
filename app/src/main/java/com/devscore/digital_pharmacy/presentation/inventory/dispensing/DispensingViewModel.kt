package com.devscore.digital_pharmacy.presentation.inventory.dispensing

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devscore.digital_pharmacy.business.domain.util.StateMessage
import com.devscore.digital_pharmacy.business.domain.util.UIComponentType
import com.devscore.digital_pharmacy.business.domain.util.doesMessageAlreadyExistInQueue
import com.devscore.digital_pharmacy.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DispensingViewModel
@Inject
constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<DispensingState> = MutableLiveData(DispensingState())

    init {
        onTriggerEvent(DispensingEvents.DeleteMedicine)
    }

    fun onTriggerEvent(event: DispensingEvents) {
        when (event) {
            is DispensingEvents.DeleteMedicine -> {
                delete()
            }
            is DispensingEvents.Count -> {
                count(event.position, event.value)
            }

            is DispensingEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is DispensingEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun count(position : Int, value: Int) {

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
            this.state.value = state.copy(page = state.page + 1)
        }
    }

    private fun onUpdateQuery(query: String) {
        state.value = state.value?.copy(query = query)
    }


    private fun delete() {
//        resetPage()
//        clearList()
//        state.value?.let { state ->
//
//            }.launchIn(viewModelScope)
        }

}