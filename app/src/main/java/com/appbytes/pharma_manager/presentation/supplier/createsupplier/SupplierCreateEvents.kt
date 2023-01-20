package com.appbytes.pharma_manager.presentation.supplier.createsupplier

import com.appbytes.pharma_manager.business.domain.models.Supplier
import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class SupplierCreateEvents {

    object NewSupplierCreate : SupplierCreateEvents()





    object NewSupplierCreateAndReturn : SupplierCreateEvents()

    data class CacheState(val supplier : Supplier): SupplierCreateEvents()

    data class Error(val stateMessage: StateMessage): SupplierCreateEvents()

    object OnRemoveHeadFromQueue: SupplierCreateEvents()
}