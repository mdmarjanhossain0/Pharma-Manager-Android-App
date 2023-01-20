package com.appbytes.pharma_manager.presentation.inventory.add.update

import com.appbytes.pharma_manager.business.domain.models.LocalMedicine
import com.appbytes.pharma_manager.business.domain.models.MedicineUnits
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class UpdateMedicineState (
    val isLoading : Boolean = false,
    val medicine : LocalMedicine = LocalMedicine(
        id = -1,
        brand_name = "",
        sku = null,
        dar_number = null,
        mr_number = null,
        generic = "",
        indication = null,
        symptom = null,
        strength = null,
        description = null,
        image = null,
        mrp = 0f,
        purchase_price = 0f,
        discount = 0f,
        is_percent_discount = false,
        manufacture = null,
        kind = null,
        form = null,
        remaining_quantity = 0f,
        damage_quantity = null,
        exp_date = null,
        rack_number = null,
        units = listOf()
    ),
    val image : String? = null,
    val unitList : List<MedicineUnits> = listOf(),
    val salesUnit : MedicineUnits? = null,
    val purchasesUnit : MedicineUnits? = null,
    val action : String = "",
    val id : Int = -1,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)