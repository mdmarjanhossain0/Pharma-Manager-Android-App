package com.devscore.digital_pharmacy.presentation.supplier.createsupplier

import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.Supplier
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

sealed class SupplierCreateEvents {

    object NewSupplierCreate : SupplierCreateEvents()

    data class CacheState(val supplier : Supplier): SupplierCreateEvents()

    data class Error(val stateMessage: StateMessage): SupplierCreateEvents()

    object OnRemoveHeadFromQueue: SupplierCreateEvents()
}