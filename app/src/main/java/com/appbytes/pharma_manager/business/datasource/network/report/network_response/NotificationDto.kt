package com.appbytes.pharma_manager.business.datasource.network.report.network_response

import com.appbytes.pharma_manager.business.domain.models.Notification
import com.google.gson.annotations.SerializedName

data class NotificationDto (
    @SerializedName("brand_name") var brand_name : String,
    @SerializedName("id") var id : Int,
    @SerializedName("type") var type : String,
    @SerializedName("message") var message : String,
    @SerializedName("stock") var stock : Float,
    @SerializedName("quantity") var quantity : Float,
    @SerializedName("date") var exp_date : String?
)




fun NotificationDto.toNotification() : Notification {
    return Notification(
        brand_name = brand_name,
        id = id,
        type = type,
        message = message,
        stock = stock,
        quantity = quantity,
        exp_date = exp_date
    )
}