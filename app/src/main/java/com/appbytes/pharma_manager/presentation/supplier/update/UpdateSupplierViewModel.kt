package com.appbytes.pharma_manager.presentation.supplier.update

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appbytes.pharma_manager.business.domain.models.Supplier
import com.appbytes.pharma_manager.business.domain.models.toCreateSupplier
import com.appbytes.pharma_manager.business.domain.util.StateMessage
import com.appbytes.pharma_manager.business.domain.util.UIComponentType
import com.appbytes.pharma_manager.business.domain.util.doesMessageAlreadyExistInQueue
import com.appbytes.pharma_manager.business.interactors.supplier.GetSupplierDetailsInteractor
import com.appbytes.pharma_manager.business.interactors.supplier.UpdateSupplierInteractor
import com.appbytes.pharma_manager.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class UpdateSupplierViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val updateSupplierInteractor: UpdateSupplierInteractor,
    private val getSupplierDetailsInteractor : GetSupplierDetailsInteractor
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<UpdateSupplierState> = MutableLiveData(UpdateSupplierState())

    private lateinit var abc : OnCompleteCallback
    fun submit(callback: OnCompleteCallback) {
        this.abc = callback
    }
    init {
    }

    fun onTriggerEvent(event: UpdateSupplierEvents) {
        when (event) {
            is UpdateSupplierEvents.Update -> {
                update(event.supplier)
            }

            is UpdateSupplierEvents.GetSupplier -> {
                getDetails(event.pk)
            }

            is UpdateSupplierEvents.CacheState -> {
                cacheState(event.supplier)
            }

            is UpdateSupplierEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is UpdateSupplierEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun getDetails(pk: Int) {
        state.value?.let { state ->
            getSupplierDetailsInteractor.execute(
                authToken = sessionManager.state.value?.authToken,
                pk = pk
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { supplier ->
                    this.state.value = state.copy(
                        supplier = supplier,
                        pk = pk
                    )
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
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

    private fun update(supplier: Supplier) {
        state.value?.let { state ->
            updateSupplierInteractor.execute(
                authToken = sessionManager.state.value?.authToken,
                pk = state.pk,
                createSupplier = supplier.toCreateSupplier()
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { supplier ->
                    abc.done()
                    this.state.value = state.copy(
                        supplier = supplier,
                        uploaded = true
                    )
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }

}

interface OnCompleteCallback {
    fun done()
}