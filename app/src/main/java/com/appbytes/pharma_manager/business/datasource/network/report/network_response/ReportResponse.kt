package com.appbytes.pharma_manager.business.datasource.network.report.network_response

import com.appbytes.pharma_manager.business.domain.models.Report
import com.google.gson.annotations.SerializedName

data class ReportResponse (

    @SerializedName("count") var count : Int?,
    @SerializedName("next") var next : String?,
    @SerializedName("previous") var previous : String?,
    @SerializedName("results") var results : List<ReportDto>,
    @SerializedName("error")var error : String? = ""

)

fun ReportResponse.toList() : List<Report> {
    val list : MutableList<Report> = mutableListOf()
    for (dto in results!!) {
        list.add(
            dto.toReport()
        )
    }
    return list
}