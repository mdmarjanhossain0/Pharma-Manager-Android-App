package com.appbytes.pharma_manager.presentation.purchases.orderlist

import com.appbytes.pharma_manager.business.domain.models.PurchasesOrder
import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class PurchasesOrderListEvents {

    object SearchNewOrder : PurchasesOrderListEvents()

    data class SearchWithQuery(val query: String) : PurchasesOrderListEvents()

    object NextPage: PurchasesOrderListEvents()

    data class PurchasesCompleted(val order : PurchasesOrder) : PurchasesOrderListEvents()

    data class UpdateQuery(val query: String): PurchasesOrderListEvents()




    data class DeleteOrder(val order : PurchasesOrder): PurchasesOrderListEvents()


    object GetOrderAndFilter: PurchasesOrderListEvents()

    data class Error(val stateMessage: StateMessage): PurchasesOrderListEvents()

    object OnRemoveHeadFromQueue: PurchasesOrderListEvents()
}