package com.appbytes.pharma_manager.presentation.cashregister.payment

import com.appbytes.pharma_manager.business.domain.models.Payment
import com.appbytes.pharma_manager.business.domain.models.Supplier
import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class PaymentEvents {

    object NewPaymentCreate : PaymentEvents()

    data class CacheState(val payment : Payment): PaymentEvents()

    data class AddSupplier(val supplier : Supplier?): PaymentEvents()

    data class AddType(val type : String) : PaymentEvents()

    data class AddDate(val date : String) : PaymentEvents()

    data class AddAmount(val amount : Float) : PaymentEvents()

    data class AddBalance(val balance : Float) : PaymentEvents()

    data class AddRemark(val remark : String) : PaymentEvents()


    data class Error(val stateMessage: StateMessage): PaymentEvents()

    object OnRemoveHeadFromQueue: PaymentEvents()
}