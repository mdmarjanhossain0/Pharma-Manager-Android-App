package com.devscore.digital_pharmacy.business.domain.models

data class PurchasesOrderMedicine (

    var pk : Int? = -1,
    var room_id : Long? = -1,
    var unit : Int,
    var quantity : Float,
    var mrp : Float,
    var purchase_price : Float,
    var local_medicine : Int,
    var brand_name : String? = null,

    var unit_name : String? = null,

    var amount : Float = 0f

)


fun PurchasesOrderMedicine.toCreatePurchasesOrderMedicine() : CreatePurchasesOderMedicine {
    return CreatePurchasesOderMedicine(
        unit = unit,
        quantity = quantity,
        mrp = mrp,
        purchase_price = purchase_price,
        local_medicine = local_medicine
    )
}