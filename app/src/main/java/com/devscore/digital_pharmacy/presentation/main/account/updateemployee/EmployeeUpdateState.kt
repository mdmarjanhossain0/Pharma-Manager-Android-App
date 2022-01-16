package com.devscore.digital_pharmacy.presentation.main.account.updateemployee

import com.devscore.digital_pharmacy.business.domain.models.Account
import com.devscore.digital_pharmacy.business.domain.models.Employee
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

data class EmployeeUpdateState(
    val isLoading : Boolean = false,
    val account: Employee? = null,
    val profile_picture : String? = null,
    val updated : Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)