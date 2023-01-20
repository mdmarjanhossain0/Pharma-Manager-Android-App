package com.appbytes.pharma_manager.business.domain.models

//data class CardMedicine(
//
//
//    var room_medicine_id : Long? = null,
//
//    var id: Int? = null,
//
//    var brand_name: String,
//
//    var sku: String?,
//
//    var dar_number: String?,
//
//    var mr_number: String? = null,
//
//    var generic: String?,
//
//    var indication: String?,
//
//    var symptom: String?,
//
//    var strength: String?,
//
//    var description: String?,
//
//
//
//    var image : String?,
//
//    var mrp: Float,
//
//    var purchase_price : Float,
//
//    var discount: Float = 0f,
//
//    var is_percent_discount: Boolean = false,
//
//    var manufacture: String?,
//
//    var kind: String?,
//
//    var form: String?,
//
//    var remaining_quantity: Float = 0f,
//
//    var damage_quantity: Float? = 0f,
//
//    var exp_date : String?,
//
//    var rack_number: String? = null,
//
//    var units: List<CardUnits>
//)
//
//
//data class CardUnits(
//    var id: Int?,
//    var room_id : Long? = -1,
//    var quantity : Int,
//    var name : String,
//    var type : String
//)
//
//
//fun LocalMedicine.toCardMedicine() : CardMedicine {
//    return CardMedicine(
//        brand_name = brand_name,
//        sku = sku,
//        dar_number = dar_number,
//        mr_number = mr_number,
//        generic = generic,
//        indication = indication,
//        symptom = symptom,
//        strength = strength,
//        description = description,
//        image = image,
//        mrp = mrp,
//        purchase_price = purchase_price,
//        discount = discount,
//        is_percent_discount = is_percent_discount,
//        manufacture = manufacture,
//        kind = kind,
//        form = form,
//        remaining_quantity = remaining_quantity,
//        damage_quantity = damage_quantity,
//        exp_date = exp_date,
//        rack_number = rack_number,
//        units = units.map { it.toCardUnits() }
//    )
//}
//
//
//fun MedicineUnits.toCardUnits() : CardUnits {
//    return CardUnits(
//        id = id,
//        name = name,
//        quantity = quantity,
//        type = type
//    )
//}
//
//
//fun CardMedicine.toLocalMedicine() : LocalMedicine {
//    return LocalMedicine(
//        brand_name = brand_name,
//        sku = sku,
//        dar_number = dar_number,
//        mr_number = mr_number,
//        generic = generic,
//        indication = indication,
//        symptom = symptom,
//        strength = strength,
//        description = description,
//        image = image,
//        mrp = mrp,
//        purchase_price = purchase_price,
//        discount = discount,
//        is_percent_discount = is_percent_discount,
//        manufacture = manufacture,
//        kind = kind,
//        form = form,
//        remaining_quantity = remaining_quantity,
//        damage_quantity = damage_quantity,
//        exp_date = exp_date,
//        rack_number = rack_number,
//        units = units.map { it.toMedicineUnit() }
//    )
//}
//
//
//fun CardUnits.toMedicineUnit() : MedicineUnits {
//    return MedicineUnits(
//        id = id,
//        name = name,
//        quantity = quantity,
//        type = type
//    )
//}