package com.devscore.digital_pharmacy.business.datasource.cache.sales

import androidx.room.Embedded
import androidx.room.Relation
import com.devscore.digital_pharmacy.business.domain.models.SalesOrder

data class SalesOderWithMedicine (

    @Embedded
    var sales_order : SalesOrderEntity,

    @Relation(
        parentColumn = "pk",
        entityColumn = "sales_order"
    )
    var sales_oder_medicines : List<SalesOrderMedicineEntity>
)


fun SalesOderWithMedicine.toSalesOder() : SalesOrder {
    return SalesOrder(
        pk = sales_order.pk,
        customer = sales_order.customer,
        customer_name = sales_order.customer_name,
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