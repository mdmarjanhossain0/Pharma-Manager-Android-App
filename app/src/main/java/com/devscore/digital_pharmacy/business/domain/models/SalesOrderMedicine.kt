package com.devscore.digital_pharmacy.business.domain.models

data class SalesOrderMedicine (

    var pk : Int? = -1,
    var room_id : Long? = -1,
    var unit : Int,
    var mrp : Float,
    var quantity : Float,
    var local_medicine : Int,
    var brand_name : String? = null,
    var unit_name : String? = null,
    var amount : Float? = 0f

)


fun SalesOrderMedicine.toCreateSalesOrderMedicine() : CreateSalesOrderMedicine {
    return CreateSalesOrderMedicine(
        unit = unit,
        quantity = quantity,
        mrp = mrp,
        local_medicine = local_medicine
    )
}