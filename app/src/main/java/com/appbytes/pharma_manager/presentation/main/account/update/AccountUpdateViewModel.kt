package com.appbytes.pharma_manager.presentation.main.account.update

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appbytes.pharma_manager.business.domain.util.StateMessage
import com.appbytes.pharma_manager.business.domain.util.UIComponentType
import com.appbytes.pharma_manager.business.domain.util.doesMessageAlreadyExistInQueue
import com.appbytes.pharma_manager.business.interactors.account.AccountUpdate
import com.appbytes.pharma_manager.business.interactors.account.GetAccount
import com.appbytes.pharma_manager.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AccountUpdateViewModel
@Inject
constructor(
    private val update: AccountUpdate,
    private val sessionManager: SessionManager,
    private val getAccount: GetAccount
) : ViewModel() {
    private val TAG: String = "AppDebug"

    val state: MutableLiveData<AccountUpdateState> = MutableLiveData(AccountUpdateState())


    init {
        getProfile()
    }

    fun onTriggerEvent(event: AccountUpdateEvents) {
        when (event) {
            is AccountUpdateEvents.AddAccountUpdate -> {
                update(
                    username = event.username,
                    mobile = event.mobile,
                    license_key = event.license_key,
                    address = event.address,
                    profile_picture = state.value?.profile_picture
                )
            }

            is AccountUpdateEvents.GetProfile -> {
                getProfile()
            }

            is AccountUpdateEvents.UpdateImage -> {
                updateImage(event.image)
            }

            is AccountUpdateEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun getProfile() {
        state.value?.let { state ->
            getAccount.execute(
                authToken = sessionManager.state.value?.authToken
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { account ->
                    this.state.value = state.copy(
                        account = account
                    )
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun updateImage(image: String?) {
        state.value?.let { state ->
            this.state.value = state.copy(
                profile_picture = image
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

    private fun update(
        username: String,
        mobile : String,
        license_key : String,
        address : String,
        profile_picture : String?
    ) {
        state.value?.let { state ->
            update.execute(
                authToken = sessionManager.state.value?.authToken,
                username = username,
                mobile = mobile,
                license_key = license_key,
                address = address,
                profile_picture = profile_picture
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { account ->
                    this.state.value = state.copy(
                        account = account,
                        updated = true
                    )
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }

}