package com.devscore.digital_pharmacy.business.datasource.network.report.network_response

import com.devscore.digital_pharmacy.business.domain.models.Notification
import com.devscore.digital_pharmacy.business.domain.models.Report
import com.google.gson.annotations.SerializedName

data class NotificationResponse (

    @SerializedName("count") var count : Int?,
    @SerializedName("next") var next : String?,
    @SerializedName("previous") var previous : String?,
    @SerializedName("results") var results : List<NotificationDto>,
    @SerializedName("error")var error : String? = ""

)

fun NotificationResponse.toList() : List<Notification> {
    val list : MutableList<Notification> = mutableListOf()
    for (dto in results!!) {
        list.add(
            dto.toNotification()
        )
    }
    return list
}