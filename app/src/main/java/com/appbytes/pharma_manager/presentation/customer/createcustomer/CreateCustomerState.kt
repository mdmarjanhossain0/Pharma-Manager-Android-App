package com.appbytes.pharma_manager.presentation.customer.createcustomer

import com.appbytes.pharma_manager.business.domain.models.Customer
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class CreateCustomerState (
    val isLoading : Boolean = false,
    val customer : Customer = Customer(
        name = "",
        email = "",
        mobile = "",
        whatsapp = "",
        facebook = "",
        imo = "",
        address = "",
        date_of_birth = "",
        total_balance = 0f,
        due_balance = 0f
    ),
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)