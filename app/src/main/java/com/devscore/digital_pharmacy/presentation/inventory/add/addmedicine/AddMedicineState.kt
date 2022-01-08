package com.devscore.digital_pharmacy.presentation.inventory.add.addmedicine

import com.devscore.digital_pharmacy.business.domain.models.GlobalMedicine
import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.MedicineUnits
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

data class AddMedicineState (
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
    val globalMedicine : GlobalMedicine? = null,
    val unitList : List<MedicineUnits> = mutableListOf(),
    val salesUnit : MedicineUnits? = null,
    val purchasesUnit : MedicineUnits? = null,
    val action : String = "",
    val id : Int = -1,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)