package com.devscore.digital_pharmacy.business.datasource.network.purchases.network_response

import com.devscore.digital_pharmacy.business.datasource.network.purchases.PurchasesOrderDto
import com.devscore.digital_pharmacy.business.domain.models.PurchasesOrder
import com.google.gson.annotations.SerializedName

data class CreatePurchasesOderResponse (

    @SerializedName("pk") var pk : Int,
    @SerializedName("vendor") var vendor : Int?,
    @SerializedName("company") var company : String?,
    @SerializedName("mobile") var mobile : String?,
    @SerializedName("total_amount") var total_amount : Float,
    @SerializedName("total_after_discount") var total_after_discount : Float,
    @SerializedName("paid_amount") var paid_amount : Float,
    @SerializedName("discount") var discount : Float,
    @SerializedName("is_discount_percent") var is_discount_percent : Boolean,
    @SerializedName("is_return") var is_return : Boolean,
    @SerializedName("purchases_order_medicines") var purchases_order_medicines : List<PurchasesOderItemDto>,
    @SerializedName("created_at")var created_at : String?,
    @SerializedName("updated_at")var updated_at : String?,
    @SerializedName("brand_name") var brand_name : String?,
    @SerializedName("status") var status : Int

)


fun CreatePurchasesOderResponse.toPurchasesOrder() : PurchasesOrder {
    return PurchasesOrder(
        pk = pk,
        vendor = vendor,
        company = company,
        mobile = mobile,
        total_amount = total_amount,
        total_after_discount = total_after_discount,
        paid_amount = paid_amount,
        discount = discount,
        is_discount_percent = is_discount_percent,
        is_return = is_return,
        status = status,
        created_at = created_at,
        updated_at = updated_at,
        purchases_order_medicines = purchases_order_medicines.map {
            it.toPurchasesOderMedicine()
        }
    )
}