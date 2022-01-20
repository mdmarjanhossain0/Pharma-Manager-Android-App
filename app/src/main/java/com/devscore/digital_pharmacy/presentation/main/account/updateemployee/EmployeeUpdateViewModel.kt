package com.devscore.digital_pharmacy.presentation.main.account.updateemployee

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devscore.digital_pharmacy.business.domain.util.StateMessage
import com.devscore.digital_pharmacy.business.domain.util.UIComponentType
import com.devscore.digital_pharmacy.business.domain.util.doesMessageAlreadyExistInQueue
import com.devscore.digital_pharmacy.business.interactors.account.GetEmployee
import com.devscore.digital_pharmacy.business.interactors.account.UpdateEmployee
import com.devscore.digital_pharmacy.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class EmployeeUpdateViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val updateEmployee : UpdateEmployee,
    private val getEmployee: GetEmployee
) : ViewModel() {
    private val TAG: String = "AppDebug"

    val state: MutableLiveData<EmployeeUpdateState> = MutableLiveData(EmployeeUpdateState())

    private lateinit var callback : OnCompleteCallback
    fun submit(callback: OnCompleteCallback) {
        this.callback = callback
    }

    fun onTriggerEvent(event: EmployeeUpdateEvents) {
        when (event) {
            is EmployeeUpdateEvents.Update -> {
                updateEmployee(
                    email = event.email,
                    username = event.username,
                    mobile = event.mobile,
                    role = event.role,
                    is_active = event.is_active
                )
            }

            is EmployeeUpdateEvents.GetEmployee -> {
                getEmployeeData(event.pk)
            }
            is EmployeeUpdateEvents.OnUpdateEmail -> {
                onUpdateEmail(event.email)
            }
            is EmployeeUpdateEvents.OnUpdateUsername -> {
                onUpdateUsername(event.username)
            }

            is EmployeeUpdateEvents.OnUpdateMobile -> {
                onUpdateMobile(event.mobile)
            }

            is EmployeeUpdateEvents.OnUpdateAddress -> {
                onUpdateAddress(event.address)
            }

            is EmployeeUpdateEvents.OnUpdateRole -> {
                onUpdateRole(event.role)
            }

            is EmployeeUpdateEvents.OnUpdateIsActive -> {
                onUpdateIsActive(event.is_active)
            }

            is EmployeeUpdateEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun getEmployeeData(pk: Int) {
        state.value?.let { state ->
            getEmployee.execute(
                authToken = sessionManager.state.value?.authToken,
                pk = pk
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { employee ->
                    this.state.value = state.copy(
                        employee = employee,
                        email = employee.email,
                        username = employee.username,
                        mobile = employee.mobile,
                        role = employee.role,
                        is_active = employee.is_active
                    )
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }


    private fun onUpdateIsActive(is_active: Boolean) {
        state.value?.let { state ->
            this.state.value = state.copy(is_active = is_active)
        }
    }

    private fun onUpdateRole(role: String) {
        state.value?.let { state ->
            this.state.value = state.copy(role = role)
        }
    }

    private fun onUpdateAddress(address: String) {
        state.value?.let { state ->
            this.state.value = state.copy(address = address)
        }
    }

    private fun onUpdateMobile(mobile: String) {
        state.value?.let { state ->
            this.state.value = state.copy(mobile = mobile)
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

    private fun updateEmployee(
        email: String,
        username: String,
        mobile: String,
        role: String?,
        is_active: Boolean
    ) {
        state.value?.let { state ->
            Log.d(TAG, email + "  " +username + "   " + mobile + " " + role + " " + is_active.toString())
            updateEmployee.execute(
                authToken = sessionManager.state.value?.authToken,
                pk = state.employee?.pk!!,
                email = email,
                username = username,
                mobile = mobile,
                role = role,
                address = state.address,
                is_active = is_active
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { employee ->
                    this.state.value = state.copy(employee = employee)
                    callback.done()
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun onUpdateUsername(username: String) {
        state.value?.let { state ->
            this.state.value = state.copy(username = username)
        }
    }

    private fun onUpdateEmail(email: String) {
        state.value?.let { state ->
            this.state.value = state.copy(email = email)
        }
    }

}

interface OnCompleteCallback {
    fun done()
}