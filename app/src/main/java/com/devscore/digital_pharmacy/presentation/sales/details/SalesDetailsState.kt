package com.devscore.digital_pharmacy.presentation.sales.details

import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

data class SalesDetailsState (
    val isLoading : Boolean = false,
    val order : SalesOrder = SalesOrder(
        pk = -2,
        customer = -1,
        customer_name = null,
        mobile = null,
        total_amount = 0f,
        total_after_discount = .0f,
        paid_amount = 0f,
        discount = 0f,
        is_discount_percent = false,
        is_return = false,
        status = 0,
        created_at = "",
        updated_at = "",
        sales_oder_medicines = ArrayList<SalesOrderMedicine>()
    ),
    val account : Account? = null,
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
    val uploaded : Boolean = false
)