package com.devscore.digital_pharmacy.presentation.inventory.add.addmedicine

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.MedicineUnits
import com.devscore.digital_pharmacy.business.domain.models.toAddMedicine
import com.devscore.digital_pharmacy.business.domain.util.MedicineProperties
import com.devscore.digital_pharmacy.business.domain.util.StateMessage
import com.devscore.digital_pharmacy.business.domain.util.UIComponentType
import com.devscore.digital_pharmacy.business.domain.util.doesMessageAlreadyExistInQueue
import com.devscore.digital_pharmacy.business.interactors.inventory.local.AddMedicineInteractor
import com.devscore.digital_pharmacy.business.interactors.inventory.FetchGlobalMedicineData
import com.devscore.digital_pharmacy.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AddMedicineViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val addMedicineInteractor: AddMedicineInteractor,
    private val fetchGlobalMedicineData: FetchGlobalMedicineData
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<AddMedicineState> = MutableLiveData(AddMedicineState())

    private lateinit var callback: Callback
    fun submit(callback: Callback) {
        this.callback = callback
    }

    init {
        addInitialUnits()
    }

    private fun addInitialUnits() {
        val unit1 = MedicineUnits(
            id = -1,
            name = "Pcs",
            quantity = 1,
            type = MedicineProperties.SALES_UNIT
        )
        updateUnitList(unit1)
        updateSalesUnit(unit1)
        val unit2 = MedicineUnits(
            id = -1,
            name = "Page",
            quantity = 10,
            type = MedicineProperties.PURCHASES_UNIT
        )
        updateUnitList(unit2)
        updatePurchasesUnit(unit2)
    }

    fun onTriggerEvent(event: AddMedicineEvents) {
        when (event) {
            is AddMedicineEvents.NewAddMedicine -> {
                addLocalMedicine()
            }

            is AddMedicineEvents.UpdateImage -> {
                updateImage(event.image)
            }

            is AddMedicineEvents.CacheState -> {
                cacheState(event.local_medicine)
            }

            is AddMedicineEvents.UpdateId -> {
                updateId(event.id)
            }

            is AddMedicineEvents.UpdateUnitList -> {
                updateUnitList(event.unit)
            }
            is AddMedicineEvents.UpdateSalesUnit -> {
                updateSalesUnit(event.unit)
            }
            is AddMedicineEvents.UpdatePurchasesUnit -> {
                updatePurchasesUnit(event.unit)
            }


            is AddMedicineEvents.RemoveUnit -> {
                removeUnit(event.unit)
            }

            is AddMedicineEvents.UpdateAction -> {
                updateAction(event.action)
            }



            is AddMedicineEvents.FetchData -> {
                fetchData()
            }

            is AddMedicineEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is AddMedicineEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun updateImage(image: String?) {
        state.value?.let { state ->
            val medicine = state.medicine.copy(
                image = image
            )
            this.state.value = state.copy(
                medicine = medicine,
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
            fetchGlobalMedicineData.execute(
                state.id
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { medicine ->
                    this.state.value = state.copy(
                        globalMedicine = medicine
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

    private fun addLocalMedicine() {
        state.value?.let { state ->
            val medicine = state.medicine.copy(
                image = state.image
            )
            this.state.value = state.copy(
                medicine = medicine
            )
        }

        Log.d(TAG, "Medicine In AddViewModel" + state.value?.medicine?.toAddMedicine())
        state.value?.let { state ->
            addMedicineInteractor.execute(
                authToken = sessionManager.state.value?.authToken,
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