package com.devscore.digital_pharmacy.business.datasource.cache.inventory.local

import androidx.room.*

@Entity(
    tableName = "Cart"
)
data class CartEntity (

    @Embedded
    val localMedicine : LocalMedicineEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "medicine_id"
    )
    var units : List<LocalMedicineUnitsEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val salesUnit : LocalMedicineUnitsEntity,

    val amount : Float,

    val quantity : Int
)