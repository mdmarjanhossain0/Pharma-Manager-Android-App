package com.devscore.digital_pharmacy.presentation.supplier.update

import com.devscore.digital_pharmacy.business.domain.models.Customer
import com.devscore.digital_pharmacy.business.domain.models.Supplier
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

sealed class UpdateSupplierEvents {

    data class Update(val supplier: Supplier) : UpdateSupplierEvents()

    data class GetSupplier(val pk : Int) : UpdateSupplierEvents()

    data class CacheState(val supplier : Supplier): UpdateSupplierEvents()

    data class Error(val stateMessage: StateMessage): UpdateSupplierEvents()

    object OnRemoveHeadFromQueue: UpdateSupplierEvents()
}