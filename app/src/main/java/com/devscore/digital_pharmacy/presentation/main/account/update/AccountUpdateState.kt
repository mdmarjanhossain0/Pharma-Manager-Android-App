package com.devscore.digital_pharmacy.presentation.main.account.update

import com.devscore.digital_pharmacy.business.domain.models.Account
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

data class AccountUpdateState(
    val isLoading : Boolean = false,
    val account: Account? = null,
    val profile_picture : String? = null,
    val updated : Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)