package com.appbytes.pharma_manager.business.datasource.cache.purchases

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.appbytes.pharma_manager.business.domain.models.PurchasesOrderMedicine

@Entity(
    tableName = "PurchasesOrderMedicine",
    foreignKeys = [
        ForeignKey(
            entity = PurchasesOrderEntity::class,
            parentColumns = ["pk"],
            childColumns = ["purchases_order"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PurchasesOrderMedicineEntity (



    @ColumnInfo(name = "purchases_order", index = true)
    var purchases_order : Int,

    @PrimaryKey(autoGenerate = false)
    var pk : Int?,

    var unit : Int,

    var quantity : Float,


    var mrp : Float,


    var purchase_price : Float,

    var local_medicine : Int,

    var brand_name : String?,

    var unit_name : String?,

    var amount : Float
)

fun PurchasesOrderMedicineEntity.toPurchasesOrderMedicine() : PurchasesOrderMedicine {
    return PurchasesOrderMedicine (
        pk = pk,
        unit = unit,
        quantity = quantity,
        mrp = mrp,
        purchase_price = purchase_price,
        local_medicine = local_medicine,
        brand_name = brand_name,
        unit_name = unit_name,
        amount = amount
    )
}