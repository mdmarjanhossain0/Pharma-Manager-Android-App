package com.devscore.digital_pharmacy.presentation.shortlist.shortlist

import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.ShortList
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

data class ShortListState(
    val isLoading : Boolean = false,
    val localMedicineList : List<ShortList> = listOf(),
    val query: String = "",
    val filter : String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)