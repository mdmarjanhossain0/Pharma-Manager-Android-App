package com.devscore.digital_pharmacy.presentation.customer.createcustomer

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devscore.digital_pharmacy.business.domain.models.Customer
import com.devscore.digital_pharmacy.business.domain.models.Supplier
import com.devscore.digital_pharmacy.business.domain.models.toCreateCustomer
import com.devscore.digital_pharmacy.business.domain.models.toCreateSupplier
import com.devscore.digital_pharmacy.business.domain.util.StateMessage
import com.devscore.digital_pharmacy.business.domain.util.UIComponentType
import com.devscore.digital_pharmacy.business.domain.util.doesMessageAlreadyExistInQueue
import com.devscore.digital_pharmacy.business.interactors.customer.CreateCustomerInteractor
import com.devscore.digital_pharmacy.business.interactors.supplier.CreateSupplierInteractor
import com.devscore.digital_pharmacy.presentation.session.SessionManager
import com.devscore.digital_pharmacy.presentation.supplier.createsupplier.SupplierCreateEvents
import com.devscore.digital_pharmacy.presentation.supplier.createsupplier.SupplierCreateState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CreateCustomerViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val createCustomerInteractor: CreateCustomerInteractor
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<CreateCustomerState> = MutableLiveData(CreateCustomerState())

    init {
    }

    fun onTriggerEvent(event: CreateCustomerEvents) {
        when (event) {
            is CreateCustomerEvents.NewCustomerCreate -> {
                createSupplier()
            }

            is CreateCustomerEvents.CacheState -> {
                cacheState(event.customer)
            }

            is CreateCustomerEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is CreateCustomerEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
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

    private fun createSupplier() {
        state.value?.let { state ->
            createCustomerInteractor.execute(
                authToken = sessionManager.state.value?.authToken,
                createCustomer = state.customer.toCreateCustomer()
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { customer ->
                    this.state.value = state.copy(customer = customer)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }

}