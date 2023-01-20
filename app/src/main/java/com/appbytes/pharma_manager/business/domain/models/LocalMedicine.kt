package com.appbytes.pharma_manager.business.domain.models

import com.appbytes.pharma_manager.business.datasource.cache.shortlist.FailureShortListEntity


data class LocalMedicine(


    var room_medicine_id : Long? = null,

    var id: Int? = null,

    var brand_name: String,

    var sku: String?,

    var dar_number: String?,

    var mr_number: String? = null,

    var generic: String?,

    var indication: String?,

    var symptom: String?,

    var strength: String?,

    var description: String?,



    var image : String?,

    var mrp: Float,

    var purchase_price : Float,

    var discount: Float = 0f,

    var is_percent_discount: Boolean = false,

    var manufacture: String?,

    var kind: String?,

    var form: String?,

    var remaining_quantity: Float = 0f,

    var damage_quantity: Float? = 0f,

    var exp_date : String?,

    var rack_number: String? = null,

    var units: List<MedicineUnits>
)


data class MedicineUnits(
    var id: Int?,
    var room_id : Long? = -1,
    var quantity : Int,
    var name : String,
    var type : String
)


fun LocalMedicine.toAddMedicine() : AddMedicine {
    return AddMedicine(
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
        purchases_price = purchase_price,
        discount = discount,
        is_percent_discount = is_percent_discount,
        manufacture = manufacture,
        kind = kind,
        form = form,
        remaining_quantity = remaining_quantity,
        damage_quantity = damage_quantity,
        exp_date = exp_date,
        rack_number = rack_number,
        units = units
    )
}




fun LocalMedicine.toFailureShortListEntity() : FailureShortListEntity {
    return FailureShortListEntity(
        id = id!!,
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
        purchases_price = purchase_price,
        discount = discount,
        is_percent_discount = is_percent_discount,
        manufacture = manufacture,
        kind = kind,
        form = form,
        remaining_quantity = remaining_quantity,
        damage_quantity = damage_quantity,
        exp_date = exp_date,
        rack_number = rack_number,
    )
}