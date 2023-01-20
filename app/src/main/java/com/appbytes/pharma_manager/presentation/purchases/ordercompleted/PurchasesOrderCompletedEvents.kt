package com.appbytes.pharma_manager.presentation.purchases.ordercompleted

import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class PurchasesOrderCompletedEvents {

    object SearchNewOrder : PurchasesOrderCompletedEvents()

    data class SearchWithQuery(val query: String) : PurchasesOrderCompletedEvents()

    object NextPage: PurchasesOrderCompletedEvents()

    data class UpdateQuery(val query: String): PurchasesOrderCompletedEvents()


    object GetOrderAndFilter: PurchasesOrderCompletedEvents()

    data class Error(val stateMessage: StateMessage): PurchasesOrderCompletedEvents()

    object OnRemoveHeadFromQueue: PurchasesOrderCompletedEvents()
}