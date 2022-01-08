package com.devscore.digital_pharmacy.business.domain.models

import com.devscore.digital_pharmacy.business.datasource.cache.sales.SalesDetailsMonthEntity
import com.devscore.digital_pharmacy.business.datasource.network.sales.network_response.SalesOderListResponse

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