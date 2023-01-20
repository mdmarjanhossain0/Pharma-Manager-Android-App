package com.appbytes.pharma_manager.presentation.inventory.local

import com.appbytes.pharma_manager.business.domain.models.LocalMedicine
import com.appbytes.pharma_manager.business.domain.models.ShortList
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class LocalMedicineState(
    val isLoading : Boolean = false,
    val localMedicineList : List<LocalMedicine> = listOf(),
    val shortList : ShortList = ShortList(
        medicine = LocalMedicine(
            id = -2,
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
        pk = -2
    ),
    val query: String = "",
    val action : String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)