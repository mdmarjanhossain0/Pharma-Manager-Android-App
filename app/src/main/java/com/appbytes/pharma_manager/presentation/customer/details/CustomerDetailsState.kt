package com.appbytes.pharma_manager.presentation.customer.details

import com.appbytes.pharma_manager.business.domain.models.Customer
import com.appbytes.pharma_manager.business.domain.models.SalesOrder
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class CustomerDetailsState (
    val isLoading : Boolean = false,
    val isLoadingList : Boolean = false,
    val orderList : List<SalesOrder> = listOf(),
    val customer : Customer? = null,
    val pk : Int = -2,
    val query: String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)