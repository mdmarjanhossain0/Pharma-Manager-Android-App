package com.appbytes.pharma_manager.presentation.main.account.employee

import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class EmployeeListEvents {

    object NewEmployeeSearch: EmployeeListEvents()

    object NextPage: EmployeeListEvents()

    data class UpdateQuery(val query: String): EmployeeListEvents()


    object GetOrderAndFilter: EmployeeListEvents()

    data class Error(val stateMessage: StateMessage): EmployeeListEvents()

    object OnRemoveHeadFromQueue: EmployeeListEvents()
}