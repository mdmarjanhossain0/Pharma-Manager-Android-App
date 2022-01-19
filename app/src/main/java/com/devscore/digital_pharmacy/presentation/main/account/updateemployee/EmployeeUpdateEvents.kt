package com.devscore.digital_pharmacy.presentation.main.account.updateemployee

import com.devscore.digital_pharmacy.business.domain.util.StateMessage
import com.devscore.digital_pharmacy.presentation.main.account.createemployee.CreateEmployeeEvents

sealed class EmployeeUpdateEvents{

    object Update : EmployeeUpdateEvents()




    data class GetEmployee(
        val pk : Int
    ) : EmployeeUpdateEvents()

    data class OnUpdateEmail(
        val email: String
    ): EmployeeUpdateEvents()

    data class OnUpdateUsername(
        val username: String
    ): EmployeeUpdateEvents()

    data class OnUpdatePassword(
        val password: String
    ): EmployeeUpdateEvents()

    data class OnUpdateConfirmPassword(
        val confirmPassword: String
    ): EmployeeUpdateEvents()

    data class OnUpdateMobile(
        val mobile: String
    ): EmployeeUpdateEvents()

    data class OnUpdateAddress(
        val address : String
    ): EmployeeUpdateEvents()

    data class OnUpdateIsActive(
        val is_active : Boolean
    ): EmployeeUpdateEvents()


    data class OnUpdateRole (
        val role : String
    ): EmployeeUpdateEvents()

    object OnRemoveHeadFromQueue: EmployeeUpdateEvents()
}