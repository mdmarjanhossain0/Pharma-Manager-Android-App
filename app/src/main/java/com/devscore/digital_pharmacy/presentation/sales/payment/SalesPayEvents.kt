package com.devscore.digital_pharmacy.presentation.sales.payment

import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

sealed class SalesPayEvents {

    object OrderCompleted : SalesPayEvents()

    data class OrderDetails(val pk : Int) : SalesPayEvents()

    data class IsDiscountPercent(val isDiscountPercent : Boolean = false) : SalesPayEvents()

    data class ReceiveAmount(val amount : Float? = 0f) : SalesPayEvents()

    data class Discount(val discount : Float? = 0f) : SalesPayEvents()

    data class DeleteMedicine(val medicine : LocalMedicine) : SalesPayEvents()


    data class SelectCustomer(val customer : Customer) : SalesPayEvents()

    data class Error(val stateMessage: StateMessage): SalesPayEvents()

    object OnRemoveHeadFromQueue: SalesPayEvents()
}