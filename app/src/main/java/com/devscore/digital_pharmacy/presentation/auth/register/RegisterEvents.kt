package com.devscore.digital_pharmacy.presentation.auth.register


sealed class RegisterEvents{

    data class Register(
        val email: String,
        val username: String,
        val shop_name : String,
        val password: String,
        val confirmPassword: String,
        val mobile : String,
        val license_key : String,
        val address : String
    ): RegisterEvents()



    data class SendOtp(
        val email: String,
        val username: String,
        val shop_name : String,
        val password: String,
        val confirmPassword: String,
        val mobile : String,
        val license_key : String,
        val address : String
    ): RegisterEvents()

    data class OnUpdateEmail(
        val email: String
    ): RegisterEvents()

    data class OnUpdateUsername(
        val username: String
    ): RegisterEvents()

    data class OnUpdatePassword(
        val password: String
    ): RegisterEvents()

    data class OnUpdateConfirmPassword(
        val confirmPassword: String
    ): RegisterEvents()

//    data class OnUpdateBusinessName(
//        val business_name: String
//    ): RegisterEvents()

    data class OnUpdateMobile(
        val mobile: String
    ): RegisterEvents()

    data class OnUpdateLicenseKey(
        val license_key : String
    ): RegisterEvents()

    data class OnUpdateAddress(
        val address : String
    ): RegisterEvents()

    data class UpdateImage(
        val image : String
    ): RegisterEvents()

    object OnRemoveHeadFromQueue: RegisterEvents()
}
