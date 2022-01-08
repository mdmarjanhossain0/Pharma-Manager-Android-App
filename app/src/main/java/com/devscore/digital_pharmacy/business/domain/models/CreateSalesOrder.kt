package com.devscore.digital_pharmacy.business.domain.models

class CreateSalesOrder (

    var customer : Int?,
    var total_amount : Float,
    var total_after_discount : Float,
    var paid_amount : Float,
    var discount : Float,
    var is_discount_percent : Boolean,
    var status : Int,
    var sales_oder_medicines : List<CreateSalesOrderMedicine>
)


data class CreateSalesOrderMedicine (
    var unit : Int,
    var quantity : Float,
    var mrp : Float,
    var local_medicine : Int

)

fun CreateSalesOrderMedicine.toSalesOrderMedicine() : SalesOrderMedicine {
    return SalesOrderMedicine(
        unit = unit,
        quantity = quantity,
        mrp = mrp,
        local_medicine = local_medicine
    )
}


fun CreateSalesOrder.toSalesOder() : SalesOrder {
    return SalesOrder(
        customer = customer,
        customer_name = null,
        mobile = null,
        total_amount = total_amount,
        total_after_discount = total_after_discount,
        paid_amount = paid_amount,
        discount = discount,
        is_discount_percent =is_discount_percent,
        is_return = false,
        status = status,
        sales_oder_medicines = sales_oder_medicines.map {
            it.toSalesOrderMedicine()
        }
    )
}