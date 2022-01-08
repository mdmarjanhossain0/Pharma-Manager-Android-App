package com.devscore.digital_pharmacy.presentation.sales.details

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devscore.digital_pharmacy.business.datasource.cache.sales.SalesDao
import com.devscore.digital_pharmacy.business.datasource.cache.sales.toSalesOder
import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.*
import com.devscore.digital_pharmacy.business.interactors.account.GetAccount
import com.devscore.digital_pharmacy.business.interactors.inventory.local.SearchLocalMedicine
import com.devscore.digital_pharmacy.business.interactors.sales.SalesOrderDetailsInteractor
import com.devscore.digital_pharmacy.business.interactors.sales.SalesReturnInteractor
import com.devscore.digital_pharmacy.presentation.sales.card.SalesCardEvents
import com.devscore.digital_pharmacy.presentation.sales.salesreturn.SalesReturnEvents
import com.devscore.digital_pharmacy.presentation.sales.salesreturn.SalesReturnState
import com.devscore.digital_pharmacy.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SalesDetailsViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val orderDetailsInteractor: SalesOrderDetailsInteractor,
    private val salesDao : SalesDao,
    private val getAccountInteractor: GetAccount
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<SalesDetailsState> = MutableLiveData(SalesDetailsState())

    init {
    }

    fun onTriggerEvent(event: SalesDetailsEvents) {
        when (event) {

            is SalesDetailsEvents.OrderDetails -> {
                getOrderDetails(event.pk)
                getAccount()
            }


            is SalesDetailsEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is SalesDetailsEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun getAccount() {
        state.value?.let { state ->
            getAccountInteractor.execute(
                authToken = sessionManager.state.value?.authToken
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
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

    private fun getOrderDetails(pk : Int) {
        state.value?.let { state ->
            orderDetailsInteractor.execute(
                authToken = sessionManager.state.value?.authToken,
                pk = pk
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { orderWithMedicine ->
                    this.state.value = state.copy(
                        order = orderWithMedicine.order)


                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
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
    
    

}