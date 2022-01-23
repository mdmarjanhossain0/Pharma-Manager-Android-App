package com.devscore.digital_pharmacy.presentation.purchases.details

import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

data class OrderDetailsState (
    val isLoading : Boolean = false,
    val pk : Int? = null,
    val order : PurchasesOrder? = null,
    val account : Account? = null,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)