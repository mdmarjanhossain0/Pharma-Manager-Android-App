package com.appbytes.pharma_manager.presentation.sales.odercompleted

import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class SalesCompletedEvents {

    object SearchOrders : SalesCompletedEvents()

    data class SearchWithQuery(val query: String) : SalesCompletedEvents()

    object NextPage: SalesCompletedEvents()

    data class UpdateQuery(val query: String): SalesCompletedEvents()


    object GetOrderAndFilter: SalesCompletedEvents()

    data class Error(val stateMessage: StateMessage): SalesCompletedEvents()

    object OnRemoveHeadFromQueue: SalesCompletedEvents()
}