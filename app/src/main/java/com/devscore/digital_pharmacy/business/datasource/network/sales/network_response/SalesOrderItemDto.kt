package com.devscore.digital_pharmacy.business.datasource.network.sales.network_response

import com.devscore.digital_pharmacy.business.datasource.network.inventory.LocalMedicineDto
import com.devscore.digital_pharmacy.business.domain.models.SalesOrderMedicine
import com.google.gson.annotations.SerializedName

data class SalesOrderItemDto (

    @SerializedName("unit") var unit : Int,
    @SerializedName("quantity") var quantity : Float,
    @SerializedName("mrp") var mrp : Float,
    @SerializedName("local_medicine") var local_medicine : Int,
    @SerializedName("brand_name") var brand_name : String,
    @SerializedName("pk") var pk : Int,
    @SerializedName("unit_name") var unit_name : String,
    @SerializedName("ammount") var amount : Float,
//    @SerializedName("details") var details : LocalMedicineDto?
    )


fun SalesOrderItemDto.toSalesOrderMedicine() : SalesOrderMedicine {
    return SalesOrderMedicine(
        pk = pk,
        unit = unit,
        quantity = quantity,
        mrp = mrp,
        local_medicine = local_medicine,
        brand_name = brand_name,
        unit_name = unit_name,
        amount = amount
    )
}