package com.devscore.digital_pharmacy.business.datasource.cache.inventory.local

import androidx.room.Embedded
import androidx.room.Relation
import com.devscore.digital_pharmacy.business.datasource.cache.shortlist.FailureShortListEntity
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.MedicineUnits

data class LocalMedicineWithUnits (

    @Embedded
    var localMedicine : LocalMedicineEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "medicine_id"
    )
    var units : List<LocalMedicineUnitsEntity>
    )


fun LocalMedicineWithUnits.toLocalMedicine() : LocalMedicine {
    return LocalMedicine(
        id = localMedicine.id,
        brand_name = localMedicine.brand_name,
        sku = localMedicine.sku,
        dar_number = localMedicine.dar_number,
        mr_number = localMedicine.mr_number,
        generic = localMedicine.generic,
        indication = localMedicine.indication,
        symptom = localMedicine.symptom,
        strength = localMedicine.strength,
        description = localMedicine.description,
        image = localMedicine.image,
        mrp = localMedicine.mrp,
        purchase_price = localMedicine.purchases_price,
        discount = localMedicine.discount,
        is_percent_discount = localMedicine.is_percent_discount,
        manufacture = localMedicine.manufacture,
        kind = localMedicine.kind,
        form = localMedicine.form,
        remaining_quantity = localMedicine.remaining_quantity,
        damage_quantity = localMedicine.damage_quantity,
        exp_date = localMedicine.exp_date,
        rack_number = localMedicine.rack_number,
        units = units.map{
            it.toMedicineUnits()
        }
    )
}


fun LocalMedicine.toLocalMedicineEntity() : LocalMedicineEntity {
    return LocalMedicineEntity(
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
        rack_number = rack_number
    )
}

fun LocalMedicine.toLocalMedicineUnitEntity() : List<LocalMedicineUnitsEntity> {
    var unitList = mutableListOf<LocalMedicineUnitsEntity>()
    for (unit in units!!) {
        unitList.add(
            LocalMedicineUnitsEntity(
                medicine_id = id!!,
                id = unit.id!!,
                name = unit.name,
                quantity = unit.quantity,
                type = unit.type
            )
        )
    }
    return unitList
}


fun LocalMedicineUnitsEntity.toMedicineUnits() : MedicineUnits {
    return MedicineUnits(
        id = id,
        name = name,
        quantity = quantity,
        type = type
    )
}


fun LocalMedicineWithUnits.toFailureShortList() : FailureShortListEntity {
    return FailureShortListEntity(
        id = localMedicine.id,
        brand_name = localMedicine.brand_name,
        sku = localMedicine.sku,
        dar_number = localMedicine.dar_number,
        mr_number = localMedicine.mr_number,
        generic = localMedicine.generic,
        indication = localMedicine.indication,
        symptom = localMedicine.symptom,
        strength = localMedicine.strength,
        description = localMedicine.description,
        image = localMedicine.image,
        mrp = localMedicine.mrp,
        purchases_price = localMedicine.purchases_price,
        discount = localMedicine.discount,
        is_percent_discount = localMedicine.is_percent_discount,
        manufacture = localMedicine.manufacture,
        kind = localMedicine.kind,
        form = localMedicine.form,
        remaining_quantity = localMedicine.remaining_quantity,
        damage_quantity = localMedicine.damage_quantity,
        exp_date = localMedicine.exp_date,
        rack_number = localMedicine.rack_number
    )
}