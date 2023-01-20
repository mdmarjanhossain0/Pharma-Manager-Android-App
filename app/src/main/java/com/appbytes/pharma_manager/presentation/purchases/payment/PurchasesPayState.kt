package com.appbytes.pharma_manager.presentation.purchases.payment

import com.appbytes.pharma_manager.business.domain.models.PurchasesOrder
import com.appbytes.pharma_manager.business.domain.models.Supplier
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class PurchasesPayState (
    val isLoading : Boolean = false,
    val order : PurchasesOrder? = null,
    val pk : Int = -2,
    val totalAmount : Float = 0f,
    val is_discount_percent : Boolean = false,
    val receivedAmount : Float = 0f,
    val discount : Float = 0f,
    val discountAmount : Float = 0f,
    val totalAmountAfterDiscount : Float = 0f,
    val vendor : Supplier? = null,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)