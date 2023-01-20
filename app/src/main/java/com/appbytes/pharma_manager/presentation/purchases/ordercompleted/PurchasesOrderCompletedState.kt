package com.appbytes.pharma_manager.presentation.purchases.ordercompleted

import com.appbytes.pharma_manager.business.domain.models.PurchasesOrder
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class PurchasesOrderCompletedState (
    val isLoading : Boolean = false,
    val orderList : List<PurchasesOrder> = listOf(),
    val query: String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)