package com.devscore.digital_pharmacy.presentation.purchases.details

import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

data class OrderDetailsState (
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
    val query: String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)