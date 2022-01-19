package com.devscore.digital_pharmacy.presentation.main.account.createemployee

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devscore.digital_pharmacy.business.domain.util.StateMessage
import com.devscore.digital_pharmacy.business.domain.util.UIComponentType
import com.devscore.digital_pharmacy.business.domain.util.doesMessageAlreadyExistInQueue
import com.devscore.digital_pharmacy.business.interactors.account.CreateEmployee
import com.devscore.digital_pharmacy.business.interactors.auth.Register
import com.devscore.digital_pharmacy.presentation.auth.register.RegisterEvents
import com.devscore.digital_pharmacy.presentation.auth.register.RegisterState
import com.devscore.digital_pharmacy.presentation.main.account.updateemployee.OnCompleteCallback
import com.devscore.digital_pharmacy.presentation.session.SessionEvents
import com.devscore.digital_pharmacy.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CreateEmployeeViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val createEmployee : CreateEmployee
) : ViewModel() {
    private val TAG: String = "AppDebug"

    val state: MutableLiveData<CreateEmployeeState> = MutableLiveData(CreateEmployeeState())

    private lateinit var callback : OnCompleteCallback
    fun submit(callback: OnCompleteCallback) {
        this.callback = callback
    }

    fun onTriggerEvent(event: CreateEmployeeEvents) {
        when (event) {
            is CreateEmployeeEvents.Create -> {
                createEmployee()
            }
            is CreateEmployeeEvents.OnUpdateEmail -> {
                onUpdateEmail(event.email)
            }
            is CreateEmployeeEvents.OnUpdateUsername -> {
                onUpdateUsername(event.username)
            }

            is CreateEmployeeEvents.OnUpdateMobile -> {
                onUpdateMobile(event.mobile)
            }

            is CreateEmployeeEvents.OnUpdateAddress -> {
                onUpdateAddress(event.address)
            }

            is CreateEmployeeEvents.OnUpdateRole -> {
                onUpdateRole(event.role)
            }

            is CreateEmployeeEvents.OnUpdatePassword -> {
                onUpdatePassword(event.password)
            }
            is CreateEmployeeEvents.OnUpdateConfirmPassword -> {
                onUpdateConfirmPassword(event.confirmPassword)
            }

            is CreateEmployeeEvents.OnUpdateIsActive -> {
                onUpdateIsActive(event.is_active)
            }
            is CreateEmployeeEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
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

    private fun createEmployee(
        email: String = state.value?.email!!,
        username: String = state.value?.username!!,
        password: String = state.value?.password!!,
        confirmPassword: String = state.value?.confirmPassword!!,
        mobile : String = state.value?.mobile!!,
        role : String = state.value?.role!!,
        address : String = state.value?.address!!,
        is_active : Boolean = state.value?.is_active!!
    ) {
        state.value?.let { state ->
            createEmployee.execute(
                authToken = sessionManager.state.value?.authToken,
                email = email,
                username = username,
                password = password,
                confirmPassword = confirmPassword,
                mobile = mobile,
                role = role,
                address = address,
                is_active = is_active
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { employee ->
                    this.state.value = state.copy(employee = employee)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun onUpdateConfirmPassword(confirmPassword: String) {
        state.value?.let { state ->
            this.state.value = state.copy(confirmPassword = confirmPassword)
        }
    }

    private fun onUpdatePassword(password: String) {
        state.value?.let { state ->
            this.state.value = state.copy(password = password)
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