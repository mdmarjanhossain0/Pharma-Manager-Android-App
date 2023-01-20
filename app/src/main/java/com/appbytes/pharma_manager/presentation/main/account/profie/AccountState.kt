package com.appbytes.pharma_manager.presentation.main.account.profie

import com.appbytes.pharma_manager.business.domain.models.Account
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class AccountState(
    val isLoading : Boolean = false,
    val account : Account? = null,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)