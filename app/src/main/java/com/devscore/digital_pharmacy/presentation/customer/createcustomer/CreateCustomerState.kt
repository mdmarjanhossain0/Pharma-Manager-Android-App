package com.devscore.digital_pharmacy.presentation.customer.createcustomer

import com.devscore.digital_pharmacy.business.domain.models.Customer
import com.devscore.digital_pharmacy.business.domain.models.Supplier
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

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