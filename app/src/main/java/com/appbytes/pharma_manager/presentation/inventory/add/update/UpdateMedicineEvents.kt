package com.appbytes.pharma_manager.presentation.inventory.add.update

import com.appbytes.pharma_manager.business.domain.models.LocalMedicine
import com.appbytes.pharma_manager.business.domain.models.MedicineUnits
import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class UpdateMedicineEvents {

    object NewUpdateMedicine : UpdateMedicineEvents()

    data class CacheState(val local_medicine : LocalMedicine): UpdateMedicineEvents()


    data class UpdateId(val id : Int) : UpdateMedicineEvents()


    data class UpdateImage(val image : String?) : UpdateMedicineEvents()

    data class UpdateUnitList(val unit : MedicineUnits) : UpdateMedicineEvents()

    data class UpdateSalesUnit(val unit : MedicineUnits) : UpdateMedicineEvents()

    data class UpdatePurchasesUnit(val unit : MedicineUnits) : UpdateMedicineEvents()

    data class RemoveUnit(val unit : MedicineUnits) : UpdateMedicineEvents()


    data class UpdateAction(val action : String) : UpdateMedicineEvents()

    object FetchData : UpdateMedicineEvents()

    data class Error(val stateMessage: StateMessage): UpdateMedicineEvents()

    object OnRemoveHeadFromQueue: UpdateMedicineEvents()
}