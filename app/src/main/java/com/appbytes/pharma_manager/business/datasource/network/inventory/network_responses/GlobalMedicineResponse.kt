package com.appbytes.pharma_manager.business.datasource.network.inventory.network_responses

import com.appbytes.pharma_manager.business.datasource.network.inventory.GlobalMedicineDto
import com.appbytes.pharma_manager.business.datasource.network.inventory.toGlobalMedicine
import com.appbytes.pharma_manager.business.domain.models.GlobalMedicine
import com.google.gson.annotations.SerializedName

data class GlobalMedicineResponse (

    @SerializedName("count") var count : Int?,
    @SerializedName("next") var next : String?,
    @SerializedName("previous") var previous : String?,
    @SerializedName("results") var results : List<GlobalMedicineDto>
)


fun GlobalMedicineResponse.toList() : List<GlobalMedicine> {
    val list : MutableList<GlobalMedicine> = mutableListOf()
    for (dto in results){
        list.add(
            dto.toGlobalMedicine()
        )
    }
    return list
}