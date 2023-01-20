package com.appbytes.pharma_manager.business.datasource.network.inventory.network_responses

import com.appbytes.pharma_manager.business.datasource.network.inventory.MedicineUnitsDto
import com.appbytes.pharma_manager.business.datasource.network.inventory.toMedicineUnits
import com.appbytes.pharma_manager.business.domain.models.LocalMedicine
import com.google.gson.annotations.SerializedName

data class AddMedicineResponse (
    @SerializedName("id") var id : Int,
    @SerializedName("brand_name") var brand_name : String,
    @SerializedName("sku") var sku : String?,
    @SerializedName("dar_number") var dar_number : String?,
    @SerializedName("mr_number") var mr_number : String?,
    @SerializedName("generic") var generic : String?,
    @SerializedName("indication") var indication : String?,
    @SerializedName("symptom") var symptom : String?,
    @SerializedName("strength") var strength : String?,
    @SerializedName("description") var description : String?,
    @SerializedName("image") var image : String?,
    @SerializedName("mrp") var mrp : Float,
    @SerializedName("purchases_price")var purchases_price : Float,
    @SerializedName("discount") var discount : Float,
    @SerializedName("is_percent_discount") var is_percent_discount : Boolean,
    @SerializedName("manufacture") var manufacture : String?,
    @SerializedName("kind") var kind : String?,
    @SerializedName("form") var form : String?,
    @SerializedName("remaining_quanity") var remaining_quantity : Float,
    @SerializedName("damage_quantity") var damage_quantity : Float?,
    @SerializedName("rack_number") var rack_number : String?,
    @SerializedName("exp_date") var exp_date : String?,
    @SerializedName("units") var units : List<MedicineUnitsDto>,
    @SerializedName("response") var response : String
)

fun AddMedicineResponse.toLocalMedicine() : LocalMedicine {
    return LocalMedicine(
        id = id,
        brand_name = brand_name,
        sku = sku,
        dar_number = dar_number,
        mr_number = mr_number,
        generic = generic,
        indication = indication,
        symptom = symptom,
        strength = strength,
        description = description,
        image = image,
        mrp = mrp,
        purchase_price = purchases_price,
        discount = discount,
        is_percent_discount = is_percent_discount,
        manufacture = manufacture,
        kind = kind,
        form = form,
        remaining_quantity = remaining_quantity,
        damage_quantity = damage_quantity,
        exp_date = exp_date,
        rack_number = rack_number,
        units = units.map {
            it.toMedicineUnits()
        }
    )
}