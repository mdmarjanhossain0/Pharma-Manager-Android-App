package com.devscore.digital_pharmacy.business.datasource.network.supplier.network_response

import com.devscore.digital_pharmacy.business.datasource.network.supplier.SupplierDto
import com.devscore.digital_pharmacy.business.datasource.network.supplier.toSupplier
import com.devscore.digital_pharmacy.business.domain.models.Supplier
import com.google.gson.annotations.SerializedName

data class SupplierListResponse (

    @SerializedName("count") var count : Int?,
    @SerializedName("next") var next : String?,
    @SerializedName("previous") var previous : String?,
    @SerializedName("results") var results : List<SupplierDto>,

    @SerializedName("detail")
    var detail: String?
)


fun SupplierListResponse.toList() : List<Supplier> {
    val list : MutableList<Supplier> = mutableListOf()
    for (dto in results){
        list.add(
            dto.toSupplier()
        )
    }
    return list
}