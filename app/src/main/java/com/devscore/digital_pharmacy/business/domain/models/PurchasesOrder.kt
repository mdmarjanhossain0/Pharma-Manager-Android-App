package com.devscore.digital_pharmacy.business.domain.models

import com.devscore.digital_pharmacy.business.datasource.cache.purchases.FailurePurchasesOrderEntity
import com.devscore.digital_pharmacy.business.datasource.cache.purchases.FailurePurchasesOrderMedicineEntity
import com.devscore.digital_pharmacy.business.datasource.cache.purchases.PurchasesOrderEntity
import com.devscore.digital_pharmacy.business.datasource.cache.purchases.PurchasesOrderMedicineEntity

data class PurchasesOrder (

    var pk : Int = -1,
    var room_id : Long? = -1,
    var vendor : Int?,
    var company : String?,
    var mobile : String?,
    var total_amount : Float,
    var total_after_discount : Float,
    var paid_amount : Float,
    var discount : Float,
    var is_discount_percent : Boolean,
    var is_return : Boolean,
    var status : Int,
    var purchases_order_medicines : List<PurchasesOrderMedicine>?,
    var created_at : String? = null,
    var updated_at : String? = null
)

fun PurchasesOrder.toPurchasesOrderEntity() : PurchasesOrderEntity {
    return PurchasesOrderEntity(
        pk = pk,
        vendor = vendor,
        company = company,
        mobile = mobile,
        total_amount = total_amount,
        total_after_discount = total_after_discount,
        paid_amount = paid_amount,
        discount = discount,
        is_discount_percent =is_discount_percent,
        is_return = is_return,
        status = status,
        created_at = created_at!!,
        updated_at = updated_at
    )
}

fun PurchasesOrder.toPurchasesOrderMedicines() : List<PurchasesOrderMedicineEntity> {
    var list = mutableListOf<PurchasesOrderMedicineEntity>()
    for (medicine in purchases_order_medicines!!) {
        list.add(
            PurchasesOrderMedicineEntity(
                purchases_order = pk!!,
                pk = medicine.pk,
                unit = medicine.unit,
                quantity = medicine.quantity,
                mrp = medicine.mrp,
                purchase_price = medicine.purchase_price,
                local_medicine = medicine.local_medicine,
                brand_name = medicine.brand_name,
                unit_name = medicine.unit_name,
                amount = medicine.amount
            )
        )
    }
    return list
}


fun PurchasesOrder.toFailurePurchasesOrderEntity() : FailurePurchasesOrderEntity {
    return FailurePurchasesOrderEntity(
        vendor = vendor,
        company = company,
        mobile = mobile,
        total_amount = total_amount,
        total_after_discount = total_after_discount,
        paid_amount = paid_amount,
        discount = discount,
        is_discount_percent =is_discount_percent,
        is_return = is_return,
        status = status,
        created_at = created_at,
        updated_at = updated_at
    )
}



fun PurchasesOrder.toFailurePurchasesOrderMedicineEntity() : List<FailurePurchasesOrderMedicineEntity> {
    var list = mutableListOf<FailurePurchasesOrderMedicineEntity>()
    for (medicine in purchases_order_medicines!!) {
        list.add(
            FailurePurchasesOrderMedicineEntity(
                purchases_order = room_id!!,
                unit = medicine.unit,
                quantity = medicine.quantity,
                mrp = medicine.mrp,
                purchase_price = medicine.purchase_price,
                local_medicine = medicine.local_medicine,
                brand_name = medicine.brand_name,
                unit_name = medicine.unit_name,
                amount = medicine.amount
            )
        )
    }
    return list
}

fun PurchasesOrder.toCreatePurchasesOrder() : CreatePurchasesOder {
    return CreatePurchasesOder(
        vendor = vendor,
        company = company,
        total_amount = total_amount,
        total_after_discount = total_after_discount,
        paid_amount = paid_amount!!,
        discount = discount,
        is_discount_percent =is_discount_percent,
        purchases_order_medicines = purchases_order_medicines!!.map {
            it.toCreatePurchasesOrderMedicine()
        }
    )
}