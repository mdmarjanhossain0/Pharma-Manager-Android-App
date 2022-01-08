package com.devscore.digital_pharmacy.presentation.supplier.update

import com.devscore.digital_pharmacy.business.domain.models.Customer
import com.devscore.digital_pharmacy.business.domain.models.Supplier
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

data class UpdateSupplierState (
    val isLoading : Boolean = false,
    val supplier : Supplier?  = null,
    val pk : Int = -1,
    val uploaded : Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)