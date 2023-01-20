package com.appbytes.pharma_manager.business.datasource.cache.purchases

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FailurePurchasesOrder")
data class FailurePurchasesOrderEntity (

    @ColumnInfo(name = "room_id")
    @PrimaryKey(autoGenerate = true)
    var room_id : Long? = null,

    @ColumnInfo(name = "customer")
    var vendor : Int?,

    @ColumnInfo(name = "company")
    var company : String?,


    @ColumnInfo(name = "mobile")
    var mobile : String?,

    @ColumnInfo(name = "total_amount")
    var total_amount : Float,

    @ColumnInfo(name = "total_after_discount")
    var total_after_discount : Float,

    @ColumnInfo(name = "paid_amount")
    var paid_amount : Float,

    @ColumnInfo(name = "discount")
    var discount : Float,

    @ColumnInfo(name = "is_discount_percent")
    var is_discount_percent : Boolean,

    @ColumnInfo(name = "is_return")
    var is_return : Boolean,

    var status : Int,

    @ColumnInfo(name = "created_at")
    var created_at : String? = null,

    @ColumnInfo(name = "updated_at")
    var updated_at : String? = null
)