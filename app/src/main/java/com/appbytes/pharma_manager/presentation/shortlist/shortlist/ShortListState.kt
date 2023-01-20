package com.appbytes.pharma_manager.presentation.shortlist.shortlist

import com.appbytes.pharma_manager.business.domain.models.ShortList
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class ShortListState(
    val isLoading : Boolean = false,
    val localMedicineList : List<ShortList> = listOf(),
    val query: String = "",
    val filter : String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)