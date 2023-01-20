package com.appbytes.pharma_manager.business.datasource.network.shortlist.network_response

import com.appbytes.pharma_manager.business.domain.models.ShortList
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