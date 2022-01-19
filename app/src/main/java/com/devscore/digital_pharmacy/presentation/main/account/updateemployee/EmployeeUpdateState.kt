package com.devscore.digital_pharmacy.presentation.main.account.updateemployee

import com.devscore.digital_pharmacy.business.domain.models.Account
import com.devscore.digital_pharmacy.business.domain.models.Employee
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

data class EmployeeUpdateState (
    val isLoading : Boolean = false,
    val employee : Employee? = null,
    val email: String = "",
    val username: String = "",
    val mobile : String = "",
    val address : String = "",
    val role : String = "",
    val is_active : Boolean = true,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)