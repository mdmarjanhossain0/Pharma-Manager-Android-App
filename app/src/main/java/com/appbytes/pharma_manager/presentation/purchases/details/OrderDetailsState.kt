package com.appbytes.pharma_manager.presentation.purchases.details

import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class OrderDetailsState (
    val isLoading : Boolean = false,
    val pk : Int? = null,
    val order : PurchasesOrder? = null,
    val account : Account? = null,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)