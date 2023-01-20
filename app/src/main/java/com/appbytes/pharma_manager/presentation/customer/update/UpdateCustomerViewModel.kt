package com.appbytes.pharma_manager.presentation.customer.update

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appbytes.pharma_manager.business.domain.models.Customer
import com.appbytes.pharma_manager.business.domain.models.toCreateCustomer
import com.appbytes.pharma_manager.business.domain.util.StateMessage
import com.appbytes.pharma_manager.business.domain.util.UIComponentType
import com.appbytes.pharma_manager.business.domain.util.doesMessageAlreadyExistInQueue
import com.appbytes.pharma_manager.business.interactors.customer.GetCustomerDetails
import com.appbytes.pharma_manager.business.interactors.customer.UpdateCustomerInteractor
import com.appbytes.pharma_manager.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class UpdateCustomerViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val updateCustomerInteractor: UpdateCustomerInteractor,
    private val getCustomerInteractor : GetCustomerDetails
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<UpdateCustomerState> = MutableLiveData(UpdateCustomerState())


    private lateinit var abc : OnCompleteCallback
    fun submit(callback: OnCompleteCallback) {
        this.abc = callback
    }
    init {
    }

    fun onTriggerEvent(event: UpdateCustomerEvents) {
        when (event) {
            is UpdateCustomerEvents.Update -> {
                update(event.customer)
            }

            is UpdateCustomerEvents.GetCustomer -> {
                getDetails(event.pk)
            }

            is UpdateCustomerEvents.CacheState -> {
                cacheState(event.customer)
            }

            is UpdateCustomerEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is UpdateCustomerEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun getDetails(pk: Int) {
        Log.d(TAG, "Details Call")
        state.value?.let { state ->
            getCustomerInteractor.execute(
                authToken = sessionManager.state.value?.authToken,
                pk = pk
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { customer ->
                    this.state.value = state.copy(
                        customer = customer,
                        pk = pk
                    )
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun cacheState(customer : Customer) {
        state.value?.let { state ->
            this.state.value = state.copy(customer = customer)
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

    private fun update(customer: Customer) {
        Log.d(TAG, "update call")
        state.value?.let { state ->
            updateCustomerInteractor.execute(
                authToken = sessionManager.state.value?.authToken,
                pk = state.pk,
                createCustomer = customer.toCreateCustomer()
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { customer ->
                    abc.done()
                    Log.d(TAG, "Data State Call")
                    if (!this.state.value?.updated!!) {
                        this.state.value = state.copy(
                            customer = customer,
                            updated = true
                        )
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
    fun done()
}