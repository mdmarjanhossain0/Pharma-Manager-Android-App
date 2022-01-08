package com.devscore.digital_pharmacy.business.datasource.cache.inventory.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "FailureMedicineUnit",
    foreignKeys = [
        ForeignKey(
            entity = FailureMedicineEntity::class,
            parentColumns = ["room_medicine_id"],
            childColumns = ["medicine_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FailureMedicineUnitEntity (


    @ColumnInfo(name = "medicine_id", index = true)
    var medicine_id : Long?,

    @PrimaryKey(autoGenerate = true)
    var room_id : Long? = null,

    var quantity : Int,
    var name : String,
    var type : String
)