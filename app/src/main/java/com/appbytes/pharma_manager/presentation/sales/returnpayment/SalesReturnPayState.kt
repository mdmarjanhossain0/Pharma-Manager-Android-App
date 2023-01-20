package com.appbytes.pharma_manager.presentation.sales.returnpayment

import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class SalesReturnPayState (
    val isLoading : Boolean = false,
    val order : SalesOrder? = null,
    val returnOrder : CreateSalesReturn? = null,
    val pk : Int = -2,
    val totalAmount : Float? = 0f,
    val is_fine_percent : Boolean = false,
    val returnAmount : Float? = 0f,
    val fine : Float? = 0f,
    val fineAmount : Float? = 0f,
    val totalAmountAfterFine : Float? = 0f,
    val customer : Customer? = null,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)