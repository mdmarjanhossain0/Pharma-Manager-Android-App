package com.appbytes.pharma_manager.presentation.auth.forgot_password

import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class ForgotPasswordState(
    val isLoading: Boolean = false,
    val isPasswordResetLinkSent: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)