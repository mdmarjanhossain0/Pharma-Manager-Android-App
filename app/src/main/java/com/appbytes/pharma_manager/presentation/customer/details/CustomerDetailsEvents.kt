package com.appbytes.pharma_manager.presentation.customer.details

import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class CustomerDetailsEvents {

    data class SearchOrders(val pk : Int) : CustomerDetailsEvents()

    data class NextPage(val pk : Int): CustomerDetailsEvents()

    data class UpdateQuery(val query: String): CustomerDetailsEvents()

    data class GetDetails(val pk : Int): CustomerDetailsEvents()


    object GetOrderAndFilter: CustomerDetailsEvents()

    data class Error(val stateMessage: StateMessage): CustomerDetailsEvents()

    object OnRemoveHeadFromQueue: CustomerDetailsEvents()
}