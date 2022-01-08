package com.devscore.digital_pharmacy.presentation.supplier.createsupplier

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devscore.digital_pharmacy.business.domain.models.Supplier
import com.devscore.digital_pharmacy.business.domain.models.toCreateSupplier
import com.devscore.digital_pharmacy.business.domain.util.StateMessage
import com.devscore.digital_pharmacy.business.domain.util.UIComponentType
import com.devscore.digital_pharmacy.business.domain.util.doesMessageAlreadyExistInQueue
import com.devscore.digital_pharmacy.business.interactors.supplier.CreateSupplierInteractor
import com.devscore.digital_pharmacy.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SupplierCreateViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val createSupplierInteractor: CreateSupplierInteractor
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<SupplierCreateState> = MutableLiveData(SupplierCreateState())

    init {
    }

    fun onTriggerEvent(event: SupplierCreateEvents) {
        when (event) {
            is SupplierCreateEvents.NewSupplierCreate -> {
                createSupplier()
            }

            is SupplierCreateEvents.CacheState -> {
                cacheState(event.supplier)
            }

            is SupplierCreateEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is SupplierCreateEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun cacheState(supplier : Supplier) {
        state.value?.let { state ->
            this.state.value = state.copy(supplier = supplier)
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

    private fun createSupplier() {
        state.value?.let { state ->
            createSupplierInteractor.execute(
                authToken = sessionManager.state.value?.authToken,
                createSupplier = state.supplier.toCreateSupplier()
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { supplier ->
                    this.state.value = state.copy(supplier = supplier)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }

}