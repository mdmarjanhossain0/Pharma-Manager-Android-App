package com.appbytes.pharma_manager.presentation.main.report

import com.appbytes.pharma_manager.business.domain.models.Report
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class ReportState(
    val isLoading : Boolean = false,
    val reportList : List<Report> = listOf(),
    val start : String = "",
    val end : String = "",
    val query: String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)