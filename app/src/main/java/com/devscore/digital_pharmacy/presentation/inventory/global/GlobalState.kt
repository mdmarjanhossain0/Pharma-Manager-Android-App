package com.devscore.digital_pharmacy.presentation.inventory.global

import com.devscore.digital_pharmacy.business.domain.models.GlobalMedicine
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.MedicineUnits
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

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