package com.appbytes.pharma_manager.presentation.inventory.global

import com.appbytes.pharma_manager.business.domain.models.GlobalMedicine
import com.appbytes.pharma_manager.business.domain.models.MedicineUnits
import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class GlobalEvents {

    object NewMedicineSearch : GlobalEvents()

    data class SearchWithQuery(val query: String) : GlobalEvents()

    object NextPage: GlobalEvents()

    data class GenericFilter(val generic : String): GlobalEvents()

    data class ManufacturerFilter(val manufacturer : String): GlobalEvents()

    data class GenericWithManufacturerFilter(val generic : String, val manufacturer: String): GlobalEvents()

    data class SetSearchSelection(val action : String) : GlobalEvents()

    data class UpdateQuery(val query: String): GlobalEvents()



    data class UpdateUnitList(val unit : MedicineUnits) : GlobalEvents()







    data class AddMedicine(val medicine : GlobalMedicine) : GlobalEvents()


    object GetOrderAndFilter: GlobalEvents()

    data class Error(val stateMessage: StateMessage): GlobalEvents()

    object OnRemoveHeadFromQueue: GlobalEvents()
}