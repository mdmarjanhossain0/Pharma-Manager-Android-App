package com.devscore.digital_pharmacy.presentation.purchases.ordercompleted

import com.devscore.digital_pharmacy.business.domain.util.StateMessage

sealed class PurchasesOrderCompletedEvents {

    object SearchNewOrder : PurchasesOrderCompletedEvents()

    data class SearchWithQuery(val query: String) : PurchasesOrderCompletedEvents()

    object NextPage: PurchasesOrderCompletedEvents()

    data class UpdateQuery(val query: String): PurchasesOrderCompletedEvents()


    object GetOrderAndFilter: PurchasesOrderCompletedEvents()

    data class Error(val stateMessage: StateMessage): PurchasesOrderCompletedEvents()

    object OnRemoveHeadFromQueue: PurchasesOrderCompletedEvents()
}