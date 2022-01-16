package com.devscore.digital_pharmacy.presentation.main.account.updateemployee

import com.devscore.digital_pharmacy.business.domain.util.StateMessage

sealed class EmployeeUpdateEvents {


    object Logout : EmployeeUpdateEvents()

    object GetProfile : EmployeeUpdateEvents()

    data class UpdateQuery(val query: String): EmployeeUpdateEvents()

    data class EmployeeUpdate(
        val username : String,
        val mobile : String,
        val address : String,
        val profile_picture : String,
        val license_key : String
    ): EmployeeUpdateEvents()

    data class UpdateImage(val image : String?) : EmployeeUpdateEvents()

    data class Error(val stateMessage: StateMessage): EmployeeUpdateEvents()

    object OnRemoveHeadFromQueue: EmployeeUpdateEvents()
}