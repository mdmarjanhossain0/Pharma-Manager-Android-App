package com.appbytes.pharma_manager.business.datasource.cache.sales

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.appbytes.pharma_manager.business.domain.models.SalesDetailsMonth

@Entity(tableName = "SalesDetailsMonth")
data class SalesDetailsMonthEntity (

    @PrimaryKey(autoGenerate = false)
    var fake_id : Int = 1,
    var total : Float,
    var due : Float,
    var sales : Int
)


fun SalesDetailsMonthEntity.toSalesDetailsMonth() : SalesDetailsMonth {
    return SalesDetailsMonth (
        fake_id = fake_id,
        total = total,
        due = due,
        sales = sales
    )
}