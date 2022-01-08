package com.devscore.digital_pharmacy.presentation.supplier.createsupplier

import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.MedicineUnits
import com.devscore.digital_pharmacy.business.domain.models.Supplier
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

data class SupplierCreateState (
    val isLoading : Boolean = false,
    val supplier : Supplier = Supplier(
        pk = -1,
        company_name = "",
        agent_name = "",
        email = "",
        mobile = "",
        whatsapp = "",
        facebook = "",
        imo = "",
        address = ""
    ),
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)