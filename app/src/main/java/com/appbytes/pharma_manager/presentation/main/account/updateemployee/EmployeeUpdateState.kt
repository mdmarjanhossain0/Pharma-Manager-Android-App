package com.appbytes.pharma_manager.presentation.main.account.updateemployee

import com.appbytes.pharma_manager.business.domain.models.Employee
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class EmployeeUpdateState (
    val isLoading : Boolean = false,
    val employee : Employee? = null,
    val email: String = "",
    val username: String = "",
    val mobile : String = "",
    val address : String = "",
    val role : String? = "",
    val is_active : Boolean = true,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)