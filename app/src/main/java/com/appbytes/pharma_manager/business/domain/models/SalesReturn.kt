package com.appbytes.pharma_manager.business.domain.models

class SalesReturn (

    var pk : Int? = -1,
    var room_id : Long? = -1,
    var customer : Int?,
    var sales_order : Int?,
    var total_amount : Float?,
    var total_after_fine : Float?,
    var return_amount : Float?,
    var fine : Float?,
    var is_fine_percent : Boolean,
    var sales_return_medicines : List<SalesOrderMedicine>?,
    var created_at : String? = null,
    var updated_at : String? = null
)



fun SalesReturn.toCreateSalesReturn() : CreateSalesReturn {
    return CreateSalesReturn(
        customer = customer,
        sales_order = sales_order,
        total_amount = total_amount,
        total_after_fine = total_after_fine,
        return_amount = return_amount!!,
        fine = fine,
        is_fine_percent = false,
        sales_return_medicines = sales_return_medicines!!.map { it.toCreateSalesOrderMedicine() }
    )
}