package com.appbytes.pharma_manager.presentation.customer.update

import com.appbytes.pharma_manager.business.domain.models.Customer
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class UpdateCustomerState (
    val isLoading : Boolean = false,
    val customer : Customer?  = null,
    val pk : Int = -1,
    val updated : Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)