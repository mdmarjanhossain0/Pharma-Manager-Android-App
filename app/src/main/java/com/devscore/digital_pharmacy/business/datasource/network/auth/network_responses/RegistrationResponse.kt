package com.devscore.digital_pharmacy.business.datasource.network.auth.network_responses

import com.google.gson.annotations.SerializedName

class RegistrationResponse(

    @SerializedName("response")
    var response: String,

    @SerializedName("error_message")
    var errorMessage: String,

    @SerializedName("email")
    var email: String,


    @SerializedName("shop_name")
    var shop_name : String,

    @SerializedName("username")
    var username: String,

    @SerializedName("pk")
    var pk: Int,

    @SerializedName("token")
    var token: String,

    @SerializedName("profile_picture")
    var profile_picture : String?,


    @SerializedName("mobile")
    var mobile : String,

    @SerializedName("license_key")
    var license_key : String?,

    @SerializedName("address")
    var address : String,


    @SerializedName("is_employee")
    var is_employee : Int,


    @SerializedName("role")
    var role : String
)