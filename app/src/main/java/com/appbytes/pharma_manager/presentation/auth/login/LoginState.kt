package com.appbytes.pharma_manager.presentation.auth.login

import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage


data class LoginState(
    val isLoading: Boolean = false,
    val email: String = "",
    val password: String = "",
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
