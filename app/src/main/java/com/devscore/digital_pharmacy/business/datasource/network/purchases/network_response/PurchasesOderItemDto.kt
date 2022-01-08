package com.devscore.digital_pharmacy.business.datasource.network.purchases.network_response

import com.devscore.digital_pharmacy.business.domain.models.PurchasesOrderMedicine
import com.google.gson.annotations.SerializedName

data class PurchasesOderItemDto (

    @SerializedName("unit") var unit : Int,
    @SerializedName("quantity") var quantity : Float,
    @SerializedName("mrp") val mrp : Float,
    @SerializedName("purchase_price") var purchase_price : Float,
    @SerializedName("local_medicine") var local_medicine : Int,
    @SerializedName("brand_name") var brand_name : String?,
    @SerializedName("pk") var pk : Int,
    @SerializedName("unit_name") var unit_name : String,
    @SerializedName("ammount") var amount : Float
    )


fun PurchasesOderItemDto.toPurchasesOderMedicine() : PurchasesOrderMedicine {
    return PurchasesOrderMedicine(
        pk = pk,
        unit = unit,
        quantity = quantity,
        mrp = mrp,
        purchase_price = purchase_price,
        local_medicine = local_medicine,
        brand_name = brand_name,
        unit_name = unit_name,
        amount = amount
    )
}