package com.appbytes.pharma_manager.presentation.main.account.update

import com.appbytes.pharma_manager.business.domain.models.Account
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class AccountUpdateState(
    val isLoading : Boolean = false,
    val account: Account? = null,
    val profile_picture : String? = null,
    val updated : Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)