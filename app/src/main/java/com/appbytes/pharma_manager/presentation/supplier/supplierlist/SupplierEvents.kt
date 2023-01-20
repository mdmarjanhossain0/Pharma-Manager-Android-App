package com.appbytes.pharma_manager.presentation.supplier.supplierlist

import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class SupplierEvents {

    object NewSearchSupplier : SupplierEvents()

    data class SearchWithQuery(val query: String) : SupplierEvents()

    object NextPage: SupplierEvents()

    data class UpdateQuery(val query: String): SupplierEvents()


    object GetOrderAndFilter: SupplierEvents()

    data class Error(val stateMessage: StateMessage): SupplierEvents()

    object OnRemoveHeadFromQueue: SupplierEvents()
}