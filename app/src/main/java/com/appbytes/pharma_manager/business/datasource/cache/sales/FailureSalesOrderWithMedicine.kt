package com.appbytes.pharma_manager.business.datasource.cache.sales

import androidx.room.Embedded
import androidx.room.Relation
import com.appbytes.pharma_manager.business.domain.models.SalesOrder

data class FailureSalesOrderWithMedicine (

    @Embedded
    var sales_order : FailureSalesOrderEntity,

    @Relation(
        parentColumn = "room_id",
        entityColumn = "sales_order"
    )
    var sales_oder_medicines : List<FailureSalesOrderMedicineEntity>
)


fun FailureSalesOrderWithMedicine.toSalesOder() : SalesOrder {
    return SalesOrder(
        room_id = sales_order.room_id,
        customer = sales_order.customer,
        mobile = sales_order.mobile,
        total_amount = sales_order.total_amount,
        total_after_discount = sales_order.total_after_discount,
        paid_amount = sales_order.paid_amount,
        discount = sales_order.discount,
        is_discount_percent = sales_order.is_discount_percent,
        is_return = sales_order.is_return,
        status = sales_order.status,
        created_at = sales_order.created_at,
        updated_at = sales_order.updated_at,
        sales_oder_medicines = sales_oder_medicines.map {
            it.toSaleOrderMedicine()
        }
    )
}