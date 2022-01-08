package com.devscore.digital_pharmacy.business.datasource.network.inventory

import com.devscore.digital_pharmacy.business.domain.models.GlobalMedicine
import com.google.gson.annotations.SerializedName

data class GlobalMedicineDto (

    @SerializedName("id") var id : Int,
    @SerializedName("brand_name") var brand_name : String?,
    @SerializedName("sku") var sku : String?,
    @SerializedName("dar_number") var darNumber : String?,
    @SerializedName("mr_number") var mrNumber : String?,
    @SerializedName("generic") var generic : String?,
    @SerializedName("indication") var indication : String?,
    @SerializedName("symptom") var symptom : String?,
    @SerializedName("strength") var strength : String?,
    @SerializedName("description") var description : String?,
    @SerializedName("mrp") var mrp : Float?,
    @SerializedName("purchase_price") var purchase_price : Float?,
    @SerializedName("manufacture") var manufacture : String?,
    @SerializedName("kind") var kind : String?,
    @SerializedName("form") var form : String?,
    @SerializedName("created_at") var createdAt : String?,
    @SerializedName("updated_at") var updatedAt : String?

)


fun GlobalMedicineDto.toGlobalMedicine() : GlobalMedicine {
    return GlobalMedicine(
        id = id,
        brand_name = brand_name,
        sku = sku,
        darNumber = darNumber,
        mrNumber = mrNumber,
        generic = generic,
        indication = indication,
        symptom = symptom,
        strength = strength,
        description = description,
        mrp = mrp,
        purchases_price = purchase_price,
        manufacture = manufacture,
        kind = kind,
        form = form,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}