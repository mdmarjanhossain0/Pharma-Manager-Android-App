package com.appbytes.pharma_manager.presentation.main.account.createemployee

sealed class CreateEmployeeEvents{

    object Create : CreateEmployeeEvents()

    data class OnUpdateEmail(
        val email: String
    ): CreateEmployeeEvents()

    data class OnUpdateUsername(
        val username: String
    ): CreateEmployeeEvents()

    data class OnUpdatePassword(
        val password: String
    ): CreateEmployeeEvents()

    data class OnUpdateConfirmPassword(
        val confirmPassword: String
    ): CreateEmployeeEvents()

    data class OnUpdateMobile(
        val mobile: String
    ): CreateEmployeeEvents()

    data class OnUpdateAddress(
        val address : String
    ): CreateEmployeeEvents()

    data class OnUpdateIsActive(
        val is_active : Boolean
    ): CreateEmployeeEvents()


    data class OnUpdateRole (
        val role : String
    ): CreateEmployeeEvents()

    object OnRemoveHeadFromQueue: CreateEmployeeEvents()
}