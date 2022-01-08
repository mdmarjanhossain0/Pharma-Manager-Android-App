package com.devscore.digital_pharmacy.presentation.customer.update

import com.devscore.digital_pharmacy.business.domain.models.Customer
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

sealed class UpdateCustomerEvents {

    data class Update(val customer: Customer) : UpdateCustomerEvents()

    data class GetCustomer(val pk : Int) : UpdateCustomerEvents()

    data class CacheState(val customer : Customer): UpdateCustomerEvents()

    data class Error(val stateMessage: StateMessage): UpdateCustomerEvents()

    object OnRemoveHeadFromQueue: UpdateCustomerEvents()
}