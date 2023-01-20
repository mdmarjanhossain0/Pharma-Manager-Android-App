package com.appbytes.pharma_manager.presentation.purchases.cart

import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class PurchasesCartState (
    val isLoading : Boolean = false,
    val order : PurchasesOrder = PurchasesOrder(
        pk = -2,
        vendor = -1,
        company = "",
        mobile = "",
        total_amount = 0f,
        total_after_discount = .0f,
        paid_amount = 0f,
        discount = 0f,
        is_discount_percent = false,
        is_return = false,
        status = 0,
        created_at = "",
        updated_at = "",
        purchases_order_medicines = ArrayList<PurchasesOrderMedicine>()
    ),
    val pk : Int = -2,
    val purchasesCartList : List<PurchasesCart> = listOf(),
    val totalAmount : Float = 0f,
    val is_discount_percent : Boolean = false,
    val receivedAmount : Float = 0f,
    val discount : Float = 0f,
    val discountAmount : Float = 0f,
    val totalAmountAfterDiscount : Float = 0f,
    val vendor : Supplier? = null,
    val medicineList : List<LocalMedicine> = listOf(),
    val query: String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
    val uploaded : Boolean = false,
    val deleted : Boolean = false
)