package com.devscore.digital_pharmacy.presentation.cashregister.payment

import com.devscore.digital_pharmacy.business.domain.models.Customer
import com.devscore.digital_pharmacy.business.domain.models.Payment
import com.devscore.digital_pharmacy.business.domain.models.Supplier
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

data class PaymentState (
    val isLoading : Boolean = false,
    val payment : Payment = Payment(
        pk = -1,
        room_id = -1,
        date = "",
        customer = -1,
        vendor = -1,
        type = "",
        total_amount = 0f,
        balance = 0f,
        remarks = "",
        created_at = "",
        updated_at = "",
        customer_name = "",
        vendor_name = ""
    ),
    val customer : Customer? = null,
    val supplier: Supplier? = null,
    val type : String? = null,
    val amount : Float = 0f,
    val balance : Float = 0f,
    val date : String? = null,
    val remark : String? = null,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)