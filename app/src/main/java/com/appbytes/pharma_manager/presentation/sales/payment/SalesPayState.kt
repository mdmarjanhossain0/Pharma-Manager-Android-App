package com.appbytes.pharma_manager.presentation.sales.payment

import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class SalesPayState (
    val isLoading : Boolean = false,
    val order : SalesOrder? = null,
    val pk : Int = -2,
    val totalAmount : Float = 0f,
    val is_discount_percent : Boolean = false,
    val receivedAmount : Float = 0f,
    val discount : Float = 0f,
    val discountAmount : Float = 0f,
    val totalAmountAfterDiscount : Float = 0f,
    val customer : Customer? = null,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)