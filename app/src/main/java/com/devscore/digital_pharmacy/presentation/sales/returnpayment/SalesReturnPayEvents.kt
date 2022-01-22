package com.devscore.digital_pharmacy.presentation.sales.returnpayment

import com.devscore.digital_pharmacy.business.domain.models.Customer
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

sealed class SalesReturnPayEvents {

    object OrderCompleted : SalesReturnPayEvents()

    data class OrderDetails(val pk : Int) : SalesReturnPayEvents()

    data class IsDiscountPercent(val isDiscountPercent : Boolean = false) : SalesReturnPayEvents()

    data class ReceiveAmount(val amount : Float? = 0f) : SalesReturnPayEvents()

    data class Discount(val discount : Float? = 0f) : SalesReturnPayEvents()

    data class DeleteMedicine(val medicine : LocalMedicine) : SalesReturnPayEvents()


    data class SelectCustomer(val customer : Customer) : SalesReturnPayEvents()

    data class Error(val stateMessage: StateMessage): SalesReturnPayEvents()

    object OnRemoveHeadFromQueue: SalesReturnPayEvents()
}