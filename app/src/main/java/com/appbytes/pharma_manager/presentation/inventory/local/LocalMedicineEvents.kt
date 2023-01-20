package com.appbytes.pharma_manager.presentation.inventory.local

import com.appbytes.pharma_manager.business.domain.models.LocalMedicine
import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class LocalMedicineEvents {

    object NewLocalMedicineSearch : LocalMedicineEvents()

    data class SearchWithQuery(val query: String) : LocalMedicineEvents()

    object NextPage: LocalMedicineEvents()

    data class UpdateQuery(val query: String): LocalMedicineEvents()


    data class AddShortList(val medicine : LocalMedicine): LocalMedicineEvents()


    data class SetSearchSelection(val action : String) : LocalMedicineEvents()



    data class DeleteLocalMedicine(val localMedicine : LocalMedicine): LocalMedicineEvents()


    object GetOrderAndFilter: LocalMedicineEvents()

    data class Error(val stateMessage: StateMessage): LocalMedicineEvents()

    object OnRemoveHeadFromQueue: LocalMedicineEvents()
}