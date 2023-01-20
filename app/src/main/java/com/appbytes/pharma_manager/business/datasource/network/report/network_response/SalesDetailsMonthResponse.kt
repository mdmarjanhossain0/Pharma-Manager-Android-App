package com.appbytes.pharma_manager.business.datasource.network.report.network_response

import com.appbytes.pharma_manager.business.domain.models.SalesDetailsMonth

data class SalesDetailsMonthResponse (
    var fake_id : Int = -1,
    var total : Float,
    var due : Float,
    var sales : Int
    )






fun SalesDetailsMonthResponse.toSalesDetailsMonth() : SalesDetailsMonth {
    return SalesDetailsMonth (
        fake_id = 1,
        total = total,
        due = due,
        sales = sales
    )
}