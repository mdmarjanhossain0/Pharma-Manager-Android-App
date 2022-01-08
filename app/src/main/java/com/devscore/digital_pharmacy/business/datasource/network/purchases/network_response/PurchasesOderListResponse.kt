package com.devscore.digital_pharmacy.business.datasource.network.purchases.network_response

import com.devscore.digital_pharmacy.business.datasource.network.purchases.PurchasesOrderDto
import com.devscore.digital_pharmacy.business.datasource.network.purchases.toPurchasesOder
import com.devscore.digital_pharmacy.business.domain.models.PurchasesOrder
import com.google.gson.annotations.SerializedName

data class PurchasesOderListResponse (

    @SerializedName("count") var count : Int?,
    @SerializedName("next") var next : String?,
    @SerializedName("previous") var previous : String?,
    @SerializedName("results") var results : List<PurchasesOrderDto>

)

fun PurchasesOderListResponse.toList() : List<PurchasesOrder> {
    val list : MutableList<PurchasesOrder> = mutableListOf()
    for (dto in results!!) {
        list.add(
            dto.toPurchasesOder()
        )
    }
    return list
}