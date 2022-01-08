package com.devscore.digital_pharmacy.business.datasource.cache.sales

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SalesCard")
data class CardEntity (








    @PrimaryKey(autoGenerate = true)
    val room_id : Long? = null,
    val medicine : Int,
    val salesUnit : Int,
    val quantity : Int,
    val amount : Float
        )