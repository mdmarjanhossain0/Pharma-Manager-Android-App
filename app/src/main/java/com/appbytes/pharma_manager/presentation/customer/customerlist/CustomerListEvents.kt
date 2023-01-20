package com.appbytes.pharma_manager.presentation.customer.customerlist

import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class CustomerListEvents {

    object NewSearchCustomer : CustomerListEvents()

    data class SearchWithQuery(val query: String) : CustomerListEvents()

    object NextPage: CustomerListEvents()

    data class UpdateQuery(val query: String): CustomerListEvents()


    object GetOrderAndFilter: CustomerListEvents()

    data class Error(val stateMessage: StateMessage): CustomerListEvents()

    object OnRemoveHeadFromQueue: CustomerListEvents()
}