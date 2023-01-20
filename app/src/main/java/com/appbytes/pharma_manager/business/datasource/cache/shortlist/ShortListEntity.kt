package com.appbytes.pharma_manager.business.datasource.cache.shortlist

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.appbytes.pharma_manager.business.datasource.cache.inventory.local.LocalMedicineWithUnits
import com.appbytes.pharma_manager.business.datasource.cache.inventory.local.toMedicineUnits
import com.appbytes.pharma_manager.business.domain.models.LocalMedicine


@Entity(tableName = "ShortList")
data class ShortListEntity (

    @PrimaryKey(autoGenerate = false)
    var pk : Int,

    @ColumnInfo(name = "id")
    var id : Int,

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


    @ColumnInfo(name = "rack_number")
    var rack_number : String?
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