package com.appbytes.pharma_manager.presentation.main.account.employee

import com.appbytes.pharma_manager.business.domain.models.Employee
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class EmployeeLIstState (
    val isLoading : Boolean = false,
    val employeeList : List<Employee> = listOf(),
    val query: String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)