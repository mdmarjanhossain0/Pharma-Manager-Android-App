package com.appbytes.pharma_manager.presentation.purchases.details

import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class OrderDetailsEvents {

    data class OrderDetails(val pk : Int) : OrderDetailsEvents()

    object DeleteOrder : OrderDetailsEvents()

    data class Error(val stateMessage: StateMessage): OrderDetailsEvents()

    object OnRemoveHeadFromQueue: OrderDetailsEvents()
}