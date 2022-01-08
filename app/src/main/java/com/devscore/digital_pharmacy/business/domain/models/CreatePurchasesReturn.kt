package com.devscore.digital_pharmacy.business.domain.models

class CreatePurchasesReturn (

    var vendor : Int?,
    var purchases_order : Int?,
    var total_amount : Float?,
    var total_after_fine : Float?,
    var return_amount : Float,
    var fine : Float?,
    var is_fine_percent : Boolean,
    var purchases_return_medicines : List<CreatePurchasesOderMedicine>
)


data class CreatePurchasesReturnMedicine (
    var unit : Int,
    var quantity : Float,
    var local_medicine : Int

)




fun CreatePurchasesReturn.toSalesReturn() : PurchasesReturn {
    return PurchasesReturn(
        vendor = vendor,
        purchases_order = purchases_order,
        total_amount = total_amount,
        total_after_fine = total_after_fine,
        return_amount = return_amount,
        fine = fine,
        is_fine_percent = is_fine_percent,
        purchases_return_medicines = purchases_return_medicines.map {
            it.toPurchasesOrderMedicine()
        }
    )
}