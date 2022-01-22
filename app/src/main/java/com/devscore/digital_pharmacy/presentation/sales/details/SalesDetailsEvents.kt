package com.devscore.digital_pharmacy.presentation.sales.details

import com.devscore.digital_pharmacy.business.domain.models.Customer
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.MedicineUnits
import com.devscore.digital_pharmacy.business.domain.models.SalesCart
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

sealed class SalesDetailsEvents {

    data class OrderDetails(val pk : Int) : SalesDetailsEvents()

    object DeleteOrder : SalesDetailsEvents()

    data class Error(val stateMessage: StateMessage): SalesDetailsEvents()

    object OnRemoveHeadFromQueue: SalesDetailsEvents()
}