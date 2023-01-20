package com.appbytes.pharma_manager.presentation.inventory.global

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appbytes.pharma_manager.business.domain.models.AddMedicine
import com.appbytes.pharma_manager.business.domain.models.GlobalMedicine
import com.appbytes.pharma_manager.business.domain.models.MedicineUnits
import com.appbytes.pharma_manager.business.domain.util.*
import com.appbytes.pharma_manager.business.interactors.inventory.global.SearchGlobalMedicine
import com.appbytes.pharma_manager.business.interactors.inventory.local.AddMedicineInteractor
import com.appbytes.pharma_manager.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import javax.inject.Inject



val PAGINATION_PAGE_SIZE = 50
@HiltViewModel
class GlobalViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val searchGlobalMedicine: SearchGlobalMedicine,
    private val addMedicineInteractor: AddMedicineInteractor
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<GlobalState> = MutableLiveData(GlobalState())
    val calender = Calendar.getInstance()

    init {
        onTriggerEvent(GlobalEvents.NewMedicineSearch)
    }

    private fun addInitialUnits() : List<MedicineUnits> {
        val units : MutableList<MedicineUnits> = mutableListOf()
        val unit1 = MedicineUnits(
            id = -1,
            name = "Pcs",
            quantity = 1,
            type = MedicineProperties.SALES_UNIT
        )
        units.add(unit1)
        val unit2 = MedicineUnits(
            id = -1,
            name = "Page",
            quantity = 10,
            type = MedicineProperties.PURCHASES_UNIT
        )
        units.add(unit2)
        return units
    }

    fun onTriggerEvent(event: GlobalEvents) {
        when (event) {
            is GlobalEvents.NewMedicineSearch -> {
                search()
            }


            is GlobalEvents.SetSearchSelection -> {
                selectQuery(event.action)
                clearList()
                resetPage()
                search()
            }

            is GlobalEvents.NextPage -> {
                incrementPageNumber()
                search()
            }

            is GlobalEvents.UpdateQuery -> {
                onUpdateQuery(event.query)
                clearList()
                resetPage()
            }
            is GlobalEvents.UpdateUnitList -> {
                updateUnitList(event.unit)
            }





            is GlobalEvents.AddMedicine -> {
                addLocalMedicine(event.medicine)
            }

            is GlobalEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is GlobalEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
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

    private fun selectQuery(action: String) {
        state.value.let { state ->
            this.state.value = state?.copy(
                action = action
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

    private fun onUpdateQueryExhausted(isExhausted: Boolean) {
        state.value?.let { state ->
            this.state.value = state.copy(isQueryExhausted = isExhausted)
        }
    }

    private fun clearList() {
        state.value?.let { state ->
            this.state.value = state.copy(globalMedicineList = listOf())
        }
    }

    private fun resetPage() {
        state.value = state.value?.copy(page = 1)
        onUpdateQueryExhausted(false)
    }

    private fun incrementPageNumber() {
        state.value?.let { state ->
            val pageNumber : Int = (state.globalMedicineList.size / PAGINATION_PAGE_SIZE) as Int + 1
            this.state.value = state.copy(page = pageNumber)
        }
    }

    private fun onUpdateQuery(query: String) {
        state.value = state.value?.copy(query = query)
    }


    private fun search() {
//        resetPage()
//        clearList()
        Log.d(TAG, "ViewModel Search Query " + state.value?.query)
        state.value?.let { state ->
            searchGlobalMedicine.execute(
                authToken = sessionManager.state.value?.authToken,
                query = state.query,
                page = state.page,
                action = state.action
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    Log.d(TAG, "ViewModel List Size " + list.size)
                    this.state.value = state.copy(
                        globalMedicineList = list,
                        isLoading = false
                    )
                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message?.contains(ErrorHandling.INVALID_PAGE) == true){
                        onUpdateQueryExhausted(true)
                    }else{
                        appendToMessageQueue(stateMessage)
                    }
                    this.state.value = state.copy(isLoading = dataState.isLoading)
                }

            }.launchIn(viewModelScope)
        }
    }






    private fun addLocalMedicine(medicine : GlobalMedicine) {
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val date = calender.get(Calendar.DATE)
        val today = year.toString() + "-" + (month + 1) + "-" + date
        val item = AddMedicine (
            brand_name = medicine.brand_name!!,
            sku = medicine.sku,
            dar_number = medicine.darNumber,
            mr_number = medicine.mrNumber,
            generic = medicine.generic,
            indication = medicine.indication,
            symptom = medicine.symptom,
            strength = medicine.strength,
            description = medicine.description,
            image = null,
            mrp = medicine.mrp!!,
            purchases_price = 0f,
            discount = 0f,
            is_percent_discount = false,
            manufacture = medicine.manufacture,
            kind = medicine.kind,
            form = medicine.form,
            remaining_quantity = 0f,
            damage_quantity = 0f,
            exp_date = today,
            rack_number = null,
            units = addInitialUnits()
                )


        Log.d(TAG, "Medicine In ViewModel" + item)
        state.value?.let { state ->
            addMedicineInteractor.execute(
                authToken = sessionManager.state.value?.authToken,
                medicine = item,
                image = null
            ).onEach { dataState ->
                Log.d(TAG, "ViewModel " + dataState.toString())
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { medicine ->
                    this.state.value = state.copy(medicine = medicine)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }
}