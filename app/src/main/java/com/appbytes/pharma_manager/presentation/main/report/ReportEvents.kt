package com.appbytes.pharma_manager.presentation.main.report

import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class ReportEvents {

    object NewReportSearch : ReportEvents()

    data class SearchWithQuery(val query: String) : ReportEvents()

    object NextPage: ReportEvents()

    data class UpdateQuery(val query: String): ReportEvents()

    data class UpdateStart(val start : String): ReportEvents()

    data class UpdateEnd(val end : String): ReportEvents()



    object GetOrderAndFilter: ReportEvents()

    data class Error(val stateMessage: StateMessage): ReportEvents()

    object OnRemoveHeadFromQueue: ReportEvents()
}