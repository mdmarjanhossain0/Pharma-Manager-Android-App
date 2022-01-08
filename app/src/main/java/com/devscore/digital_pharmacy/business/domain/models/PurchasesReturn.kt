package com.devscore.digital_pharmacy.business.domain.models

class PurchasesReturn (

    var pk : Int? = -1,
    var room_id : Long? = -1,
    var vendor : Int?,
    var purchases_order : Int?,
    var total_amount : Float?,
    var total_after_fine : Float?,
    var return_amount : Float?,
    var fine : Float?,
    var is_fine_percent : Boolean,
    var purchases_return_medicines : List<PurchasesOrderMedicine>?,
    var created_at : String? = null,
    var updated_at : String? = null
)



fun PurchasesReturn.toCreatePurchasesReturn() : CreatePurchasesReturn {
    return CreatePurchasesReturn(
        vendor = vendor,
        purchases_order = purchases_order,
        total_amount = total_amount,
        total_after_fine = total_after_fine,
        return_amount = return_amount!!,
        fine = fine,
        is_fine_percent = false,
        purchases_return_medicines = purchases_return_medicines!!.map { it.toCreatePurchasesOrderMedicine() }
    )
}