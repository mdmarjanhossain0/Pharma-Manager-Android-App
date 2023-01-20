package com.appbytes.pharma_manager.presentation.auth.register

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appbytes.pharma_manager.business.domain.util.StateMessage
import com.appbytes.pharma_manager.business.domain.util.UIComponentType
import com.appbytes.pharma_manager.business.domain.util.doesMessageAlreadyExistInQueue
import com.appbytes.pharma_manager.business.interactors.auth.Register
import com.appbytes.pharma_manager.business.interactors.auth.SendOtp
import com.appbytes.pharma_manager.presentation.session.SessionEvents
import com.appbytes.pharma_manager.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel
@Inject
constructor(
    private val register: Register,
    private val sessionManager: SessionManager,
    private val sendOtp: SendOtp
) : ViewModel() {
    private val TAG: String = "AppDebug"

    val state: MutableLiveData<RegisterState> = MutableLiveData(RegisterState())





    private lateinit var callback: RegisterCallback
    fun submit(callback: RegisterCallback) {
        this.callback = callback
    }

    fun onTriggerEvent(event: RegisterEvents) {
        when (event) {
            is RegisterEvents.Register -> {
                register(
                    email = event.email,
                    shop_name = event.shop_name,
                    username = event.username,
                    password = event.password,
                    confirmPassword = event.confirmPassword,
                    mobile = event.mobile,
                    license_key = event.license_key,
                    address = event.address,
                    image = state.value?.image
                )
            }









            is RegisterEvents.SendOtp -> {
                updateData(
                    email = event.email,
                    shop_name = event.shop_name,
                    username = event.username,
                    password = event.password,
                    confirmPassword = event.confirmPassword,
                    mobile = event.mobile,
                    license_key = event.license_key,
                    address = event.address,
                )
                register(
                    email = event.email,
                    shop_name = event.shop_name,
                    username = event.username,
                    password = event.password,
                    confirmPassword = event.confirmPassword,
                    mobile = event.mobile,
                    license_key = event.license_key,
                    address = event.address,
                    image = state.value?.image
                )
            }


            is RegisterEvents.UpdateImage -> {
                updateImage(event.image)
            }
            is RegisterEvents.OnUpdateEmail -> {
                onUpdateEmail(event.email)
            }
            is RegisterEvents.OnUpdateUsername -> {
                onUpdateUsername(event.username)
            }
            is RegisterEvents.OnUpdatePassword -> {
                onUpdatePassword(event.password)
            }
            is RegisterEvents.OnUpdateConfirmPassword -> {
                onUpdateConfirmPassword(event.confirmPassword)
            }
            is RegisterEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun updateData(
        email: String,
        username: String,
        shop_name : String,
        password: String,
        confirmPassword: String,
        mobile : String,
        license_key : String?,
        address : String
    ) {
        state.value?.let { state ->
            this.state.value = state.copy(
                email = email,
                username = username,
                shop_name = shop_name,
                password = password,
                confirmPassword = confirmPassword,
                mobile = mobile,
                license_key = license_key,
                address = address
            )
        }
    }

    private fun updateImage(image: String) {
        state.value?.let { state ->
            this.state.value = state.copy(
                image = image
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

    private fun send(
        email: String,
        username: String,
        shop_name : String,
        password: String,
        confirmPassword: String,
        mobile : String,
        license_key : String?,
        address : String,
        image: String?
    ) {
        state.value?.let { state ->
            sendOtp.execute(
                email = email,
                shop_name = shop_name,
                username = username,
                password = password,
                confirmPassword = confirmPassword,
                mobile = mobile,
                license_key = license_key,
                address = address,
                image = image
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { response ->
                    callback.done()
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun register(
        email: String,
        username: String,
        shop_name : String,
        password: String,
        confirmPassword: String,
        mobile : String,
        license_key : String?,
        address : String,
        image: String?
    ) {
        state.value?.let { state ->
            register.execute(
                email = email,
                shop_name = shop_name,
                username = username,
                password = password,
                confirmPassword = confirmPassword,
                mobile = mobile,
                license_key = license_key,
                address = address,
                image = image
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { authToken ->
                    sessionManager.onTriggerEvent(SessionEvents.Login(authToken))
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

interface RegisterCallback {
    fun done()
}