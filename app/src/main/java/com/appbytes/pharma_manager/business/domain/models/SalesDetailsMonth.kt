package com.appbytes.pharma_manager.business.domain.models

import com.appbytes.pharma_manager.business.datasource.cache.sales.SalesDetailsMonthEntity

data class SalesDetailsMonth (

    var fake_id : Int = -1,
    var total : Float,
    var due : Float,
    var sales : Int
)





fun SalesDetailsMonth.toSalesDetailsMonthEntity() : SalesDetailsMonthEntity {
    return SalesDetailsMonthEntity (
        fake_id = fake_id,
        total = total,
        due = due,
        sales = sales
    )
}