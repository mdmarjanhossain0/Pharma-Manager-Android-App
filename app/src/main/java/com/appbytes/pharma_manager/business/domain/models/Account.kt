package com.appbytes.pharma_manager.business.domain.models

data class Account(
    val pk: Int,
    val email: String,
    val shop_name : String,
    val username: String,
    val profile_picture : String?,
    val mobile : String,
    val license_key : String?,
    val address : String,
    val is_employee : Int,
    val role : String,
    val file : String?
)









