package com.devscore.digital_pharmacy.business.datasource.network.inventory.network_responses

import com.devscore.digital_pharmacy.business.datasource.network.inventory.LocalMedicineDto
import com.devscore.digital_pharmacy.business.datasource.network.inventory.toLocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
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