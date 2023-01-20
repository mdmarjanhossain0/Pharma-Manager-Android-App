package com.appbytes.pharma_manager.presentation.sales.details

import com.appbytes.pharma_manager.business.domain.models.*
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class SalesDetailsState (
    val isLoading : Boolean = false,
    val pk : Int? = null,
    val order : SalesOrder? = null,
    val account : Account? = null,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)