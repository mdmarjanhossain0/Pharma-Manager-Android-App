package com.appbytes.pharma_manager.business.domain.models

class CreateSalesReturn (

    var customer : Int?,
    var sales_order : Int?,
    var total_amount : Float?,
    var total_after_fine : Float?,
    var return_amount : Float,
    var fine : Float?,
    var is_fine_percent : Boolean,
    var sales_return_medicines : List<CreateSalesOrderMedicine>
)


data class CreateSalesReturnMedicine (
    var unit : Int,
    var quantity : Float,
    var local_medicine : Int

)




fun CreateSalesReturn.toSalesReturn() : SalesReturn {
    return SalesReturn(
        customer = customer,
        sales_order = sales_order,
        total_amount = total_amount,
        total_after_fine = total_after_fine,
        return_amount = return_amount,
        fine = fine,
        is_fine_percent = is_fine_percent,
        sales_return_medicines = sales_return_medicines.map {
            it.toSalesOrderMedicine()
        }
    )
}