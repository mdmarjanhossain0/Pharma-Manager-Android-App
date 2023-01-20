package com.appbytes.pharma_manager.presentation.auth.register

import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage


data class RegisterState(
    val isLoading: Boolean = false,
    val email: String = "",
    val username: String = "",
    val shop_name : String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val mobile : String = "",
    val license_key : String? = "",
    val address : String = "",
    val image : String? = null,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
