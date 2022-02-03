package com.devscore.digital_pharmacy.presentation.inventory.add.update

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.MedicineUnits
import com.devscore.digital_pharmacy.business.domain.models.toAddMedicine
import com.devscore.digital_pharmacy.business.domain.util.StateMessage
import com.devscore.digital_pharmacy.business.domain.util.UIComponentType
import com.devscore.digital_pharmacy.business.domain.util.doesMessageAlreadyExistInQueue
import com.devscore.digital_pharmacy.business.interactors.inventory.local.FetchLocalMedicineData
import com.devscore.digital_pharmacy.business.interactors.inventory.local.UpdateMedicineInteractor
import com.devscore.digital_pharmacy.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class UpdateMedicineViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val updateMedicineInteractor: UpdateMedicineInteractor,
    private val fetchLocalMedicineData: FetchLocalMedicineData
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<UpdateMedicineState> = MutableLiveData(UpdateMedicineState())


    private lateinit var callback: Callback

    fun submit(callback: Callback) {
        this.callback = callback
    }

    init {
    }

    fun onTriggerEvent(event: UpdateMedicineEvents) {
        when (event) {
            is UpdateMedicineEvents.NewUpdateMedicine -> {
                updateMedicine()
            }

            is UpdateMedicineEvents.UpdateImage -> {
                updateImage(event.image)
            }

            is UpdateMedicineEvents.CacheState -> {
                cacheState(event.local_medicine)
            }

            is UpdateMedicineEvents.UpdateId -> {
                updateId(event.id)
            }

            is UpdateMedicineEvents.UpdateUnitList -> {
                updateUnitList(event.unit)
            }
            is UpdateMedicineEvents.UpdateSalesUnit -> {
                updateSalesUnit(event.unit)
            }
            is UpdateMedicineEvents.UpdatePurchasesUnit -> {
                updatePurchasesUnit(event.unit)
            }


            is UpdateMedicineEvents.RemoveUnit -> {
                removeUnit(event.unit)
            }

            is UpdateMedicineEvents.UpdateAction -> {
                updateAction(event.action)
            }



            is UpdateMedicineEvents.FetchData -> {
                fetchData()
            }

            is UpdateMedicineEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is UpdateMedicineEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun updateImage(image: String?) {
        state.value?.let { state ->
            this.state.value = state.copy(
                image = image
            )
        }
    }

    private fun updateAction(action: String) {
        state.value?.let { state ->
            this.state.value = state.copy(
                action = action
            )
        }
    }

    private fun removeUnit(unit: MedicineUnits) {
        state.value?.let { state ->
            val list = state.unitList.toMutableList()
            list.remove(unit)
            this.state.value = state.copy(
                unitList = list
            )
        }
    }

    private fun updatePurchasesUnit(unit: MedicineUnits) {
        state.value?.let { state ->
            this.state.value = state.copy(
                purchasesUnit = unit
            )
        }
    }

    private fun updateSalesUnit(unit: MedicineUnits) {
        state.value?.let { state ->
            this.state.value = state.copy(
                salesUnit = unit
            )
        }
    }

    private fun updateUnitList(unit : MedicineUnits) {
        state.value?.let { state ->
            val list = state.unitList.toMutableList()
            list.add(unit)
            this.state.value = state.copy(
                unitList = list
            )
        }
    }

    private fun fetchData() {
        state.value?.let { state ->
            fetchLocalMedicineData.execute(
                state.id
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { medicine ->
                    this.state.value = state.copy(
                        medicine = medicine,
                        id = medicine.id!!,
                        unitList = medicine.units
                    )
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun updateId(id: Int) {
        state.value?.let { state ->
            this.state.value = state.copy(
                id = id
            )
        }
    }

    private fun cacheState(local_medicine : LocalMedicine) {
        state.value?.let { state ->
            this.state.value = state.copy(medicine = local_medicine)
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

    private fun updateMedicine() {
        state.value?.let { state ->
            updateMedicineInteractor.execute(
                authToken = sessionManager.state.value?.authToken,
                id = state.id,
                medicine = state.medicine.toAddMedicine(),
                image = state.image
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { medicine ->
                    this.state.value = state.copy(medicine = medicine)
                    callback.done()
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }

}







interface Callback {
    fun done()
}