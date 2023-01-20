package com.appbytes.pharma_manager.presentation.supplier.createsupplier

import com.appbytes.pharma_manager.business.domain.models.Supplier
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class SupplierCreateState (
    val isLoading : Boolean = false,
    val supplier : Supplier = Supplier(
        pk = -1,
        company_name = "",
        agent_name = "",
        email = "",
        mobile = "",
        whatsapp = "",
        facebook = "",
        imo = "",
        address = ""
    ),
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)