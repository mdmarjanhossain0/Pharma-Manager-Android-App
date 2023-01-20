package com.appbytes.pharma_manager.presentation.cashregister.receive

import com.appbytes.pharma_manager.business.domain.models.Customer
import com.appbytes.pharma_manager.business.domain.models.Receive
import com.appbytes.pharma_manager.business.domain.models.Supplier
import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class ReceiveEvents {

    object NewReceiveCreate : ReceiveEvents()

    data class CacheState(val receive : Receive): ReceiveEvents()

    data class AddCustomer(val customer : Customer?): ReceiveEvents()


    data class AddSupplier(val supplier : Supplier): ReceiveEvents()

    data class AddType(val type : String) : ReceiveEvents()

    data class AddDate(val date : String) : ReceiveEvents()

    data class AddAmount(val amount : Float) : ReceiveEvents()

    data class AddBalance(val balance : Float?) : ReceiveEvents()

    data class AddRemark(val remark : String) : ReceiveEvents()

    data class Error(val stateMessage: StateMessage): ReceiveEvents()

    object OnRemoveHeadFromQueue: ReceiveEvents()
}