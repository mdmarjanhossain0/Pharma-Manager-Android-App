package com.devscore.digital_pharmacy.business.domain.models

data class Employee (
    val pk: Int,
    val email: String,
    val username: String,
    val profile_picture : String?,
//    val business_name : String,
    val mobile : String,
    val license_key : String,
    val address : String?,
    val is_employee : Int,
    val role : String?,
    val is_active : Boolean
)