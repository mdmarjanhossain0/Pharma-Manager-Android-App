package com.devscore.digital_pharmacy.business.datasource.network.shortlist.network_response

import com.devscore.digital_pharmacy.business.datasource.network.inventory.LocalMedicineDto
import com.devscore.digital_pharmacy.business.datasource.network.inventory.toLocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.ShortList
import com.google.gson.annotations.SerializedName

data class ShortListNetworkResponse (

    @SerializedName("count") var count : Int?,
    @SerializedName("next") var next : String?,
    @SerializedName("previous") var previous : String?,
    @SerializedName("results") var results : List<ShortListDto>

)

fun ShortListNetworkResponse.toList() : List<ShortList> {
    val list : MutableList<ShortList> = mutableListOf()
    for (dto in results!!) {
        list.add(
            dto.toShortList()
        )
    }
    return list
}