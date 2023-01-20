package com.appbytes.pharma_manager.presentation.sales.details

import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class SalesDetailsEvents {

    data class OrderDetails(val pk : Int) : SalesDetailsEvents()

    object DeleteOrder : SalesDetailsEvents()

    data class Error(val stateMessage: StateMessage): SalesDetailsEvents()

    object OnRemoveHeadFromQueue: SalesDetailsEvents()
}