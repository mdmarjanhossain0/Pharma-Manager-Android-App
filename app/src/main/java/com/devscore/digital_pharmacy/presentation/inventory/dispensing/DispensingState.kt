package com.devscore.digital_pharmacy.presentation.inventory.dispensing

import com.devscore.digital_pharmacy.business.domain.models.DispensingMedicine
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

data class DispensingState (
    val isLoading : Boolean = false,
    val localMedicineList : List<DispensingMedicine> = listOf(),
    val query: String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)