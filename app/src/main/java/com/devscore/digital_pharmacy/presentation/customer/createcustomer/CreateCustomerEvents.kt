package com.devscore.digital_pharmacy.presentation.customer.createcustomer

import com.devscore.digital_pharmacy.business.domain.models.Customer
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

sealed class CreateCustomerEvents {

    object NewCustomerCreate : CreateCustomerEvents()

    data class CacheState(val customer : Customer): CreateCustomerEvents()

    data class Error(val stateMessage: StateMessage): CreateCustomerEvents()

    object OnRemoveHeadFromQueue: CreateCustomerEvents()
}