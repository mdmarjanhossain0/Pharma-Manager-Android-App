package com.appbytes.pharma_manager.business.datasource.network.report.network_response

import com.appbytes.pharma_manager.business.domain.models.Notification
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