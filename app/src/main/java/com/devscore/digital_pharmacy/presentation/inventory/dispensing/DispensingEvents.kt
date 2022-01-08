package com.devscore.digital_pharmacy.presentation.inventory.dispensing

import com.devscore.digital_pharmacy.business.domain.util.StateMessage

sealed class DispensingEvents {

    object DeleteMedicine : DispensingEvents()

    data class Count(val position : Int, val value : Int): DispensingEvents()

    object NextPage: DispensingEvents()

    data class UpdateQuery(val query: String): DispensingEvents()


    object GetOrderAndFilter: DispensingEvents()

    data class Error(val stateMessage: StateMessage): DispensingEvents()

    object OnRemoveHeadFromQueue: DispensingEvents()
}