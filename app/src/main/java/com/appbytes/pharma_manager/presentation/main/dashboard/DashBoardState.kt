package com.appbytes.pharma_manager.presentation.main.dashboard

import com.appbytes.pharma_manager.business.domain.models.SalesDetailsMonth
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class DashBoardState(
    val isLoading : Boolean = false,
    val details : SalesDetailsMonth = SalesDetailsMonth(
        total = 0f,
        due = 0f,
        sales = 0
    ),
    val local : Int = 0,
    val sales : Int = 0,
    val purchases : Int = 0,
    val customer : Int = 0,
    val supplier : Int = 0,
    val query: String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)