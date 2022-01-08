package com.devscore.digital_pharmacy.business.datasource.network.report.network_response

import com.devscore.digital_pharmacy.business.datasource.network.inventory.LocalMedicineDto
import com.devscore.digital_pharmacy.business.datasource.network.inventory.toLocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.Report
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