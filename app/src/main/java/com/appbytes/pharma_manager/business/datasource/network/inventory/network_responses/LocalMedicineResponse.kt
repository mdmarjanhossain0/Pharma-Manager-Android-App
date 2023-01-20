package com.appbytes.pharma_manager.business.datasource.network.inventory.network_responses

import com.appbytes.pharma_manager.business.datasource.network.inventory.LocalMedicineDto
import com.appbytes.pharma_manager.business.datasource.network.inventory.toLocalMedicine
import com.appbytes.pharma_manager.business.domain.models.LocalMedicine
import com.google.gson.annotations.SerializedName

data class LocalMedicineResponse (

    @SerializedName("count") var count : Int?,
    @SerializedName("next") var next : String?,
    @SerializedName("previous") var previous : String?,
    @SerializedName("results") var results : List<LocalMedicineDto>

)

fun LocalMedicineResponse.toList() : List<LocalMedicine> {
    val list : MutableList<LocalMedicine> = mutableListOf()
    for (dto in results!!) {
        list.add(
            dto.toLocalMedicine()
        )
    }
    return list
}