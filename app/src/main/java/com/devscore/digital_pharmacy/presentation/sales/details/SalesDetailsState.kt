package com.devscore.digital_pharmacy.presentation.sales.details

import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

data class SalesDetailsState (
    val isLoading : Boolean = false,
    val pk : Int? = null,
    val order : SalesOrder? = null,
    val account : Account? = null,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)