package com.devscore.digital_pharmacy.presentation.supplier.details

import com.devscore.digital_pharmacy.business.domain.models.Customer
import com.devscore.digital_pharmacy.business.domain.models.PurchasesOrder
import com.devscore.digital_pharmacy.business.domain.models.SalesOrder
import com.devscore.digital_pharmacy.business.domain.models.Supplier
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

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