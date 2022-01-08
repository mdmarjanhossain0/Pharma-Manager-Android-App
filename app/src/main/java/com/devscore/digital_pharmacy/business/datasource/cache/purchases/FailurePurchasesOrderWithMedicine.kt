package com.devscore.digital_pharmacy.business.datasource.cache.purchases

import androidx.room.Embedded
import androidx.room.Relation
import com.devscore.digital_pharmacy.business.domain.models.PurchasesOrder


data class FailurePurchasesOrderWithMedicine (

    @Embedded
    var purchases_oder : FailurePurchasesOrderEntity,

    @Relation(
        parentColumn = "room_id",
        entityColumn = "purchases_order"
    )
    var purchases_order_medicines : List<FailurePurchasesOrderMedicineEntity>
)


fun FailurePurchasesOrderWithMedicine.toPurchasesOrder() : PurchasesOrder {
    return PurchasesOrder (
        room_id = purchases_oder.room_id,
        vendor = purchases_oder.vendor,
        company = purchases_oder.company,
        mobile = purchases_oder.mobile,
        total_amount = purchases_oder.total_amount,
        total_after_discount = purchases_oder.total_after_discount,
        paid_amount = purchases_oder.paid_amount,
        discount = purchases_oder.discount,
        is_discount_percent = purchases_oder.is_discount_percent,
        is_return = purchases_oder.is_return,
        status = purchases_oder.status,
        created_at = purchases_oder.created_at,
        updated_at = purchases_oder.updated_at,
        purchases_order_medicines = purchases_order_medicines.map {
            it.toPurchasesOrderMedicine()
        }
    )
}