package com.appbytes.pharma_manager.business.datasource.network.purchases.network_response

import com.appbytes.pharma_manager.business.domain.models.PurchasesReturn
import com.google.gson.annotations.SerializedName

data class CreatePurchasesReturnResponse (

    @SerializedName("pk") var pk : Int,
    @SerializedName("vendor") var vendor : Int?,
    @SerializedName("purchases_order") var purchases_order : Int?,
    @SerializedName("total_amount") var total_amount : Float,
    @SerializedName("total_after_fine") var total_after_fine : Float,
    @SerializedName("return_amount") var return_amount : Float,
    @SerializedName("fine") var fine : Float,
    @SerializedName("is_fine_percent") var is_fine_percent : Boolean,
    @SerializedName("purchases_return_medicines") var purchases_return_medicines : List<PurchasesOderItemDto>,
    @SerializedName("created_at")var created_at : String?,
    @SerializedName("updated_at")var updated_at : String?,
    @SerializedName("brand_name") var brand_name : String?
)





fun CreatePurchasesReturnResponse.toPurchasesReturn() : PurchasesReturn {
    return PurchasesReturn(
        pk = pk,
        vendor = vendor,
        purchases_order = purchases_order,
        total_amount = total_amount,
        total_after_fine = total_after_fine,
        return_amount = return_amount,
        fine = fine,
        is_fine_percent = is_fine_percent,
        created_at = created_at,
        updated_at = updated_at,
        purchases_return_medicines = purchases_return_medicines.map {
            it.toPurchasesOderMedicine()
        }
    )
}