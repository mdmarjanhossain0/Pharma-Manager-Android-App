package com.appbytes.pharma_manager.presentation.supplier.details

import com.appbytes.pharma_manager.business.domain.models.PurchasesOrder
import com.appbytes.pharma_manager.business.domain.models.Supplier
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class SupplierDetailsState (
    val isLoading : Boolean = false,
    val isLoadingList : Boolean = false,
    val orderList : List<PurchasesOrder> = listOf(),
    val supplier : Supplier? = null,
    val pk : Int = -2,
    val query: String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)