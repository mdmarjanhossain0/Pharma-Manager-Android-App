package com.devscore.digital_pharmacy.presentation.main.dashboard

import com.devscore.digital_pharmacy.business.domain.models.SalesDetailsMonth
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

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