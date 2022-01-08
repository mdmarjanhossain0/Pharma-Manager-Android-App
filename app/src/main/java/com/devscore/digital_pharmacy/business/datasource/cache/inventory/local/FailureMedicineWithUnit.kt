package com.devscore.digital_pharmacy.business.datasource.cache.inventory.local

import androidx.room.Embedded
import androidx.room.Relation
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.MedicineUnits

data class FailureMedicineWithUnit (

    @Embedded
    var failureMedicine : FailureMedicineEntity,

    @Relation(
        parentColumn = "room_medicine_id",
        entityColumn = "medicine_id"
    )
    var units : List<FailureMedicineUnitEntity>
)


fun FailureMedicineWithUnit.toLocalMedicine() : LocalMedicine {
    return LocalMedicine(
        room_medicine_id = failureMedicine.room_medicine_id,
        brand_name = failureMedicine.brand_name,
        sku = failureMedicine.sku,
        dar_number = failureMedicine.dar_number,
        mr_number = failureMedicine.mr_number,
        generic = failureMedicine.generic,
        indication = failureMedicine.indication,
        symptom = failureMedicine.symptom,
        strength = failureMedicine.strength,
        description = failureMedicine.description,
        image = failureMedicine.image,
        mrp = failureMedicine.mrp,
        purchase_price = failureMedicine.purchases_price,
        discount = failureMedicine.discount,
        is_percent_discount = failureMedicine.is_percent_discount,
        manufacture = failureMedicine.manufacture,
        kind = failureMedicine.kind,
        form = failureMedicine.form,
        remaining_quantity = failureMedicine.remaining_quantity,
        damage_quantity = failureMedicine.damage_quantity,
        exp_date = failureMedicine.exp_date,
        rack_number = failureMedicine.rack_number,
        units = units.map{
            it.toMedicineUnits()
        }
    )
}


fun LocalMedicine.toFailureMedicine() : FailureMedicineEntity {
    return FailureMedicineEntity(
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

fun LocalMedicine.toFailureMedicineUnitEntity() : List<FailureMedicineUnitEntity> {
    var unitList = mutableListOf<FailureMedicineUnitEntity>()
    for (unit in units!!) {
        unitList.add(
            FailureMedicineUnitEntity(
                medicine_id = room_medicine_id!!,
                room_id = unit.room_id,
                name = unit.name,
                quantity = unit.quantity,
                type = unit.type
            )
        )
    }
    return unitList
}


fun FailureMedicineUnitEntity.toMedicineUnits() : MedicineUnits {
    return MedicineUnits(
        room_id = room_id,
        id = -1,
        name = name,
        quantity = quantity,
        type = type
    )
}