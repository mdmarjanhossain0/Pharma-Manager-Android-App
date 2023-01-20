package com.appbytes.pharma_manager.business.datasource.cache.inventory.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FailureMedicine")
data class FailureMedicineEntity (


    @ColumnInfo(name = "room_medicine_id")
    @PrimaryKey(autoGenerate = true)
    var room_medicine_id : Long? = null,

    @ColumnInfo(name = "brand_name")
    var brand_name : String,

    @ColumnInfo(name = "sku")
    var sku : String?,

    @ColumnInfo(name = "dar_number")
    var dar_number : String?,


    @ColumnInfo(name = "mr_number")
    var mr_number : String?,


    @ColumnInfo(name = "generic")
    var generic : String?,

    @ColumnInfo(name = "indication")
    var indication : String?,

    @ColumnInfo(name = "symptom")
    var symptom : String?,

    @ColumnInfo(name = "strength")
    var strength : String?,

    @ColumnInfo(name = "description")
    var description : String?,






    @ColumnInfo(name = "image")
    var image : String?,

    @ColumnInfo(name = "base_mrp")
    var mrp : Float,

    @ColumnInfo(name = "purchases_price")
    var purchases_price : Float,

    @ColumnInfo(name = "discount")
    var discount : Float,

    @ColumnInfo(name = "id_percent_discount")
    var is_percent_discount : Boolean,

    @ColumnInfo(name = "manufacture")
    var manufacture : String?,

    @ColumnInfo(name = "kind")
    var kind : String?,

    @ColumnInfo(name = "form")
    var form : String?,

    @ColumnInfo(name = "remaining_quantity")
    var remaining_quantity : Float,

    @ColumnInfo(name = "damage_quantity")
    var damage_quantity : Float?,





    @ColumnInfo(name = "exp_date")
    var exp_date : String?,


    @ColumnInfo(name = "rack_number")
    var rack_number : String?

)