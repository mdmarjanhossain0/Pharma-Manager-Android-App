package com.devscore.digital_pharmacy.business.datasource.cache.purchases

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.devscore.digital_pharmacy.business.domain.models.PurchasesOrderMedicine

@Entity(
    tableName = "FailurePurchasesOrderMedicine",
    foreignKeys = [
        ForeignKey(
            entity = FailurePurchasesOrderEntity::class,
            parentColumns = ["room_id"],
            childColumns = ["purchases_order"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FailurePurchasesOrderMedicineEntity (



    @ColumnInfo(name = "purchases_order", index = true)
    var purchases_order : Long?,


    @PrimaryKey(autoGenerate = true)
    var room_id : Long? = null,

    var unit : Int,

    var quantity : Float,

    var mrp : Float,

    var purchase_price : Float,

    var local_medicine : Int,

    var brand_name : String?,

    var unit_name : String?,

    var amount : Float

)

fun FailurePurchasesOrderMedicineEntity.toPurchasesOrderMedicine() : PurchasesOrderMedicine {
    return PurchasesOrderMedicine (
        room_id = room_id,
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