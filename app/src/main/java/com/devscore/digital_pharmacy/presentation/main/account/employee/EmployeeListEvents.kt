package com.devscore.digital_pharmacy.presentation.main.account.employee

import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

sealed class EmployeeListEvents {

    object NewEmployeeSearch: EmployeeListEvents()

    object NextPage: EmployeeListEvents()

    data class UpdateQuery(val query: String): EmployeeListEvents()


    object GetOrderAndFilter: EmployeeListEvents()

    data class Error(val stateMessage: StateMessage): EmployeeListEvents()

    object OnRemoveHeadFromQueue: EmployeeListEvents()
}