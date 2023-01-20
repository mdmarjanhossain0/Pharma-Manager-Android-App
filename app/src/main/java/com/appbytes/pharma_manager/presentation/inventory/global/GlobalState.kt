package com.appbytes.pharma_manager.presentation.inventory.global

import com.appbytes.pharma_manager.business.domain.models.GlobalMedicine
import com.appbytes.pharma_manager.business.domain.models.LocalMedicine
import com.appbytes.pharma_manager.business.domain.models.MedicineUnits
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class GlobalState(
    val isLoading : Boolean = false,
    val globalMedicineList : List<GlobalMedicine> = listOf(),
    val medicine : LocalMedicine? = null,
    val action : String = "",
    val query: String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
    val unitList : List<MedicineUnits> = mutableListOf()
)