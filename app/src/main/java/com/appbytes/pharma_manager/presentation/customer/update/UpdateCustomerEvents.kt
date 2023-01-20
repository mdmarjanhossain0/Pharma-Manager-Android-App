package com.appbytes.pharma_manager.presentation.customer.update

import com.appbytes.pharma_manager.business.domain.models.Customer
import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class UpdateCustomerEvents {

    data class Update(val customer: Customer) : UpdateCustomerEvents()

    data class GetCustomer(val pk : Int) : UpdateCustomerEvents()

    data class CacheState(val customer : Customer): UpdateCustomerEvents()

    data class Error(val stateMessage: StateMessage): UpdateCustomerEvents()

    object OnRemoveHeadFromQueue: UpdateCustomerEvents()
}