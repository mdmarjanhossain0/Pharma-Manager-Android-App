package com.appbytes.pharma_manager.presentation.supplier.update

import com.appbytes.pharma_manager.business.domain.models.Supplier
import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class UpdateSupplierEvents {

    data class Update(val supplier: Supplier) : UpdateSupplierEvents()

    data class GetSupplier(val pk : Int) : UpdateSupplierEvents()

    data class CacheState(val supplier : Supplier): UpdateSupplierEvents()

    data class Error(val stateMessage: StateMessage): UpdateSupplierEvents()

    object OnRemoveHeadFromQueue: UpdateSupplierEvents()
}