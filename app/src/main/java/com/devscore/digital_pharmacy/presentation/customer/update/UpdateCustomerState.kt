package com.devscore.digital_pharmacy.presentation.customer.update

import com.devscore.digital_pharmacy.business.domain.models.Customer
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

data class UpdateCustomerState (
    val isLoading : Boolean = false,
    val customer : Customer?  = null,
    val pk : Int = -1,
    val updated : Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)