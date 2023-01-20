package com.appbytes.pharma_manager.business.domain.models

import com.appbytes.pharma_manager.business.datasource.cache.sales.*

data class SalesOrder (

    var pk : Int? = -1,
    var room_id : Long? = -1,
    var customer : Int?,
    var customer_name : String? = null,
    var mobile : String?,
    var total_amount : Float,
    var total_after_discount : Float,
    var paid_amount : Float,
    var discount : Float,
    var is_discount_percent : Boolean,
    var is_return : Boolean,
    var status : Int,
    var sales_oder_medicines : List<SalesOrderMedicine>?,
    var created_at : String? = null,
    var updated_at : String? = null
)



fun SalesOrder.toSalesOrderEntity() : SalesOrderEntity {
    return SalesOrderEntity(
        pk = pk!!,
        customer = customer,
        customer_name = customer_name,
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

fun SalesOrder.toSalesOrderMedicinesEntity() : List<SalesOrderMedicineEntity> {
    var list = mutableListOf<SalesOrderMedicineEntity>()
    for (medicine in sales_oder_medicines!!) {
        list.add(
            SalesOrderMedicineEntity(
                sales_order = pk!!,
                pk = medicine.pk,
                unit = medicine.unit,
                quantity = medicine.quantity,
                mrp = medicine.mrp,
                local_medicine = medicine.local_medicine,
                brand_name = medicine.brand_name,
                unit_name = medicine.unit_name,
                amount = medicine.amount
            )
        )
    }
    return list
}


fun SalesOrder.toFailureSalesOrderEntity() : FailureSalesOrderEntity {
    return FailureSalesOrderEntity(
        customer = customer,
        customer_name = customer_name,
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



fun SalesOrder.toFailureSalesOderMedicineEntity() : List<FailureSalesOrderMedicineEntity> {
    var list = mutableListOf<FailureSalesOrderMedicineEntity>()
    for (medicine in sales_oder_medicines!!) {
        list.add(
            FailureSalesOrderMedicineEntity(
                sales_order = room_id!!,
                unit = medicine.unit,
                quantity = medicine.quantity,
                mrp = medicine.mrp,
                local_medicine = medicine.local_medicine,
                brand_name = medicine.brand_name,
                unit_name = medicine.unit_name,
                amount = medicine.amount
            )
        )
    }
    return list
}

fun SalesOrder.toCreateSalesOrder() : CreateSalesOrder {
    return CreateSalesOrder(
        customer = customer,
        total_amount = total_amount,
        total_after_discount = total_after_discount,
        paid_amount = paid_amount!!,
        discount = discount,
        is_discount_percent =is_discount_percent,
        status = status,
        sales_oder_medicines = sales_oder_medicines!!.map {
            it.toCreateSalesOrderMedicine()
        }
    )
}