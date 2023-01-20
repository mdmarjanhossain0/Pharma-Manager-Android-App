package com.appbytes.pharma_manager.presentation.supplier.details

import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class SupplierDetailsEvents {

    data class SearchOrders(val pk : Int) : SupplierDetailsEvents()

    data class NextPage(val pk : Int): SupplierDetailsEvents()

    data class UpdateQuery(val query: String): SupplierDetailsEvents()

    data class GetDetails(val pk : Int): SupplierDetailsEvents()


    object GetOrderAndFilter: SupplierDetailsEvents()

    data class Error(val stateMessage: StateMessage): SupplierDetailsEvents()

    object OnRemoveHeadFromQueue: SupplierDetailsEvents()
}