package com.devscore.digital_pharmacy.business.domain.models

import com.devscore.digital_pharmacy.business.datasource.cache.shortlist.ShortListEntity

data class ShortList (
    var medicine : LocalMedicine,
    var pk : Int? = -1,
    var room_id : Long? = -1
        )


fun ShortList.toShortListEntity() : ShortListEntity {
    return ShortListEntity(
        pk = pk!!,
        id = medicine.id!!,
        brand_name = medicine.brand_name,
        sku = medicine.sku,
        dar_number = medicine.dar_number,
        mr_number = medicine.mr_number,
        generic = medicine.generic,
        indication = medicine.indication,
        symptom = medicine.symptom,
        strength = medicine.strength,
        description = medicine.description,
        mrp = medicine.mrp,
        purchases_price = medicine.purchase_price,
        discount = medicine.discount,
        is_percent_discount = medicine.is_percent_discount,
        manufacture = medicine.manufacture,
        kind = medicine.kind,
        form = medicine.form,
        remaining_quantity = medicine.remaining_quantity,
        damage_quantity = medicine.damage_quantity,
        rack_number = medicine.rack_number,
    )
}




fun ShortList.toLocalMedicine() : LocalMedicine {
    return this.medicine
}