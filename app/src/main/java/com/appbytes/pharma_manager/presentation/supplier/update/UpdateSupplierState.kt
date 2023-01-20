package com.appbytes.pharma_manager.presentation.supplier.update

import com.appbytes.pharma_manager.business.domain.models.Supplier
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class UpdateSupplierState (
    val isLoading : Boolean = false,
    val supplier : Supplier?  = null,
    val pk : Int = -1,
    val uploaded : Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)