package com.devscore.digital_pharmacy.business.datasource.cache.sales

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.devscore.digital_pharmacy.business.domain.models.SalesOrderMedicine


@Entity(
    tableName = "SalesOrderMedicine",
    foreignKeys = [
        ForeignKey(
            entity = SalesOrderEntity::class,
            parentColumns = ["pk"],
            childColumns = ["sales_order"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SalesOrderMedicineEntity (


    @ColumnInfo(name = "sales_order", index = true)
    var sales_order : Int,

    @PrimaryKey(autoGenerate = false)
    var pk : Int?,

    var unit : Int,

    var quantity : Float,

    var mrp : Float,

    var local_medicine : Int,

    var brand_name : String?,

    var unit_name : String?,

    var amount : Float?

)

fun SalesOrderMedicineEntity.toSaleOrderMedicine() : SalesOrderMedicine {
    return SalesOrderMedicine(
        pk = pk,
        unit = unit,
        quantity = quantity,
        mrp = mrp,
        local_medicine = local_medicine,
        brand_name = brand_name,
        unit_name = unit_name,
        amount = amount
    )
}