package com.devscore.digital_pharmacy.business.datasource.network.sales.network_response

import com.devscore.digital_pharmacy.business.datasource.network.sales.SalesOrderDto
import com.devscore.digital_pharmacy.business.datasource.network.sales.toSalesOrder
import com.devscore.digital_pharmacy.business.domain.models.SalesOrder
import com.google.gson.annotations.SerializedName

data class SalesOderListResponse (

    @SerializedName("count") var count : Int?,
    @SerializedName("next") var next : String?,
    @SerializedName("previous") var previous : String?,
    @SerializedName("results") var results : List<SalesOrderDto>

)

fun SalesOderListResponse.toList() : List<SalesOrder> {
    val list : MutableList<SalesOrder> = mutableListOf()
    for (dto in results) {
        list.add(
            dto.toSalesOrder()
        )
    }
    return list
}