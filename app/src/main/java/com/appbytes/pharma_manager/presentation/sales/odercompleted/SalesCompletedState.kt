package com.appbytes.pharma_manager.presentation.sales.odercompleted

import com.appbytes.pharma_manager.business.domain.models.SalesOrder
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class SalesCompletedState (
    val isLoading : Boolean = false,
    val orderList : List<SalesOrder> = listOf(),
    val query: String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)