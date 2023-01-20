package com.appbytes.pharma_manager.presentation.customer.createcustomer

import com.appbytes.pharma_manager.business.domain.models.Customer
import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class CreateCustomerEvents {

    object NewCustomerCreate : CreateCustomerEvents()






    object NewCustomerCreateAndReturn : CreateCustomerEvents()

    data class CacheState(val customer : Customer): CreateCustomerEvents()

    data class Error(val stateMessage: StateMessage): CreateCustomerEvents()

    object OnRemoveHeadFromQueue: CreateCustomerEvents()
}