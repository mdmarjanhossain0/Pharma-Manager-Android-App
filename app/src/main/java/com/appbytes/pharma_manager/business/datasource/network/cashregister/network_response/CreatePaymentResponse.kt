package com.appbytes.pharma_manager.business.datasource.network.cashregister.network_response

import com.appbytes.pharma_manager.business.domain.models.Payment
import com.google.gson.annotations.SerializedName

data class CreatePaymentResponse (
    @SerializedName("pk") var pk : Int,
    @SerializedName("date") var date : String,
    @SerializedName("customer") var customer : Int?,
    @SerializedName("vendor") var vendor : Int?,
    @SerializedName("type") var type : String,
    @SerializedName("total_amount") var total_amount : Float,
    @SerializedName("balance") var balance : Float,
    @SerializedName("remarks") var remarks : String,
    @SerializedName("created_at") var created_at : String,
    @SerializedName("updated_at") var updated_at : String,
    @SerializedName("customer_name") var customer_name : String?,
    @SerializedName("vendor_name") var vendor_name : String?,
    @SerializedName("response") var response : String,
)



fun CreatePaymentResponse.toPayment() : Payment {
    return Payment(
        pk = pk,
        date = date,
        customer = customer,
        vendor = vendor,
        type = type,
        total_amount = total_amount,
        balance = balance,
        remarks = remarks,
        created_at = created_at,
        updated_at = updated_at,
        customer_name = customer_name,
        vendor_name = vendor_name
    )
}