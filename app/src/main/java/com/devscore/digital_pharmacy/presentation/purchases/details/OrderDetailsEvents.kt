package com.devscore.digital_pharmacy.presentation.purchases.details

import com.devscore.digital_pharmacy.business.domain.util.StateMessage

sealed class OrderDetailsEvents {

    data class OrderDetails(val pk : Int) : OrderDetailsEvents()

    object DeleteOrder : OrderDetailsEvents()

    data class Error(val stateMessage: StateMessage): OrderDetailsEvents()

    object OnRemoveHeadFromQueue: OrderDetailsEvents()
}