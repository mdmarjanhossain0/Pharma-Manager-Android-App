package com.devscore.digital_pharmacy.business.datasource.network.account.network_response

import com.devscore.digital_pharmacy.business.domain.models.Employee
import com.google.gson.annotations.SerializedName

class EmployeeDto(

    @SerializedName("response")
    var response: String,

    @SerializedName("error_message")
    var errorMessage: String,

    @SerializedName("email")
    var email: String,

    @SerializedName("username")
    var username: String,

    @SerializedName("pk")
    var pk: Int,

    @SerializedName("token")
    var token: String,

    @SerializedName("profile_picture")
    var profile_picture : String?,

//    @SerializedName("business_name")
//    var business_name : String,

    @SerializedName("mobile")
    var mobile : String,

    @SerializedName("license_key")
    var license_key : String,

    @SerializedName("address")
    var address : String,


    @SerializedName("is_employee")
    var is_employee : Int,

    @SerializedName("role")
    var role : String?
)



fun EmployeeDto.toEmployee() : Employee {
    return Employee(
        pk = pk,
        email = email,
        username = username,
        profile_picture = profile_picture,
//        business_name = business_name,
        mobile = mobile,
        license_key = license_key,
        address = address,
        is_employee = is_employee,
        role = role
    )
}