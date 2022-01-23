package com.devscore.digital_pharmacy.presentation.purchases.payment

import com.devscore.digital_pharmacy.business.domain.models.Customer
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.Supplier
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

sealed class PurchasesPayEvents {

    object OrderCompleted : PurchasesPayEvents()

    data class OrderDetails(val pk : Int) : PurchasesPayEvents()

    data class IsDiscountPercent(val isDiscountPercent : Boolean = false) : PurchasesPayEvents()

    data class ReceiveAmount(val amount : Float? = 0f) : PurchasesPayEvents()

    data class Discount(val discount : Float? = 0f) : PurchasesPayEvents()

    data class DeleteMedicine(val medicine : LocalMedicine) : PurchasesPayEvents()


    data class SelectSupplier(val vendor : Supplier) : PurchasesPayEvents()

    data class Error(val stateMessage: StateMessage): PurchasesPayEvents()

    object OnRemoveHeadFromQueue: PurchasesPayEvents()
}