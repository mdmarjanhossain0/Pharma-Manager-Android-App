package com.devscore.digital_pharmacy.presentation.main.account.createemployee

import com.devscore.digital_pharmacy.business.domain.models.Customer
import com.devscore.digital_pharmacy.business.domain.models.Employee
import com.devscore.digital_pharmacy.business.domain.models.Receive
import com.devscore.digital_pharmacy.business.domain.models.Supplier
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

data class CreateEmployeeState (
    val isLoading : Boolean = false,
    val employee : Employee? = null,
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val mobile : String = "",
    val address : String = "",
    val role : String = "",
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)