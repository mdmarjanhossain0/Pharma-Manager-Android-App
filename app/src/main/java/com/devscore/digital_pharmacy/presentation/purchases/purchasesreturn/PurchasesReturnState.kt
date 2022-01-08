package com.devscore.digital_pharmacy.presentation.purchases.purchasesreturn

import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

data class PurchasesReturnState (
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
    val returnOrder : CreatePurchasesReturn = CreatePurchasesReturn(
        vendor = -1,
        purchases_order = null,
        total_amount = 0f,
        total_after_fine = .0f,
        return_amount = 0f,
        fine = 0f,
        is_fine_percent = false,
        purchases_return_medicines = ArrayList<CreatePurchasesOderMedicine>()
    ),
    val purchasesCartList : List<PurchasesCart> = listOf(),
    val totalAmount : Float? = 0f,
    val is_fine_percent : Boolean = false,
    val returnAmount : Float? = 0f,
    val fine : Float? = 0f,
    val fineAmount : Float? = 0f,
    val totalAmountAfterFine : Float? = 0f,
    val vendor : Supplier? = null,
    val medicineList : List<LocalMedicine> = listOf(),
    val query: String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
    val uploaded : Boolean = false
)