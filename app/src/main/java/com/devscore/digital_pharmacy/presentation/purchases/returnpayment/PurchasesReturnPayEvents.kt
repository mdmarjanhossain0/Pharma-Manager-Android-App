package com.devscore.digital_pharmacy.presentation.purchases.returnpayment

import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.Supplier
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

sealed class PurchasesReturnPayEvents {

    object OrderCompleted : PurchasesReturnPayEvents()

    data class OrderDetails(val pk : Int) : PurchasesReturnPayEvents()

    data class IsDiscountPercent(val isDiscountPercent : Boolean = false) : PurchasesReturnPayEvents()

    data class ReceiveAmount(val amount : Float? = 0f) : PurchasesReturnPayEvents()

    data class Discount(val discount : Float? = 0f) : PurchasesReturnPayEvents()

    data class DeleteMedicine(val medicine : LocalMedicine) : PurchasesReturnPayEvents()


    data class SelectVendor(val vendor : Supplier) : PurchasesReturnPayEvents()

    data class Error(val stateMessage: StateMessage): PurchasesReturnPayEvents()

    object OnRemoveHeadFromQueue: PurchasesReturnPayEvents()
}