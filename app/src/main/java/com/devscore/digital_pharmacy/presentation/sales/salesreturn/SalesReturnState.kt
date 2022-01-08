package com.devscore.digital_pharmacy.presentation.sales.salesreturn

import com.devscore.digital_pharmacy.business.domain.models.*
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

data class SalesReturnState (
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
    val returnOrder : CreateSalesReturn = CreateSalesReturn(
        customer = -1,
        sales_order = null,
        total_amount = 0f,
        total_after_fine = .0f,
        return_amount = 0f,
        fine = 0f,
        is_fine_percent = false,
        sales_return_medicines = ArrayList<CreateSalesOrderMedicine>()
    ),
    val pk : Int = -2,
    val salesCartList : List<SalesCart> = listOf(),
    val totalAmount : Float? = 0f,
    val is_fine_percent : Boolean = false,
    val returnAmount : Float? = 0f,
    val fine : Float? = 0f,
    val fineAmount : Float? = 0f,
    val totalAmountAfterFine : Float? = 0f,
    val customer : Customer? = null,
    val medicineList : List<LocalMedicine> = listOf(),
    val query: String = "",
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
    val uploaded : Boolean = false
)