package com.appbytes.pharma_manager.presentation.inventory.dispensing

import com.appbytes.pharma_manager.business.domain.models.DispensingMedicine
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class DispensingState (
    val isLoading : Boolean = false,
    val localMedicineList : List<DispensingMedicine> = listOf(),
    val query: String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)