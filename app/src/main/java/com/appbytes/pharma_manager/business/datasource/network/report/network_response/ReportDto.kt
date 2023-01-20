package com.appbytes.pharma_manager.business.datasource.network.report.network_response

import com.appbytes.pharma_manager.business.domain.models.Report
import com.google.gson.annotations.SerializedName

data class ReportDto (


    @SerializedName("pk")
    var pk : Int,



    @SerializedName("amount")
    var amount : Float,


    @SerializedName("type")
    var type : String,



    @SerializedName("details")
    var details : String?,


    @SerializedName("remark")
    var remark : String?,


    @SerializedName("created_at")
    var created_at : String
)





fun ReportDto.toReport() : Report {
    return Report(
        pk = pk,
        amount = amount,
        type = type,
        details = details,
        remark = remark,
        created_at = created_at
    )
}