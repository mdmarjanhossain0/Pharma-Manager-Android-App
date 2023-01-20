package com.appbytes.pharma_manager.presentation.inventory.add.addmedicine

import com.appbytes.pharma_manager.business.domain.models.LocalMedicine
import com.appbytes.pharma_manager.business.domain.models.MedicineUnits
import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class AddMedicineEvents {

    object NewAddMedicine : AddMedicineEvents()

    data class CacheState(val local_medicine : LocalMedicine): AddMedicineEvents()


    data class UpdateId(val id : Int) : AddMedicineEvents()


    data class UpdateImage(val image : String?) : AddMedicineEvents()

    data class UpdateUnitList(val unit : MedicineUnits) : AddMedicineEvents()

    data class UpdateSalesUnit(val unit : MedicineUnits) : AddMedicineEvents()

    data class UpdatePurchasesUnit(val unit : MedicineUnits) : AddMedicineEvents()

    data class RemoveUnit(val unit : MedicineUnits) : AddMedicineEvents()


    data class UpdateAction(val action : String) : AddMedicineEvents()

    object FetchData : AddMedicineEvents()

    data class Error(val stateMessage: StateMessage): AddMedicineEvents()

    object OnRemoveHeadFromQueue: AddMedicineEvents()
}