package com.devscore.digital_pharmacy.presentation.sales.orderlist

import com.devscore.digital_pharmacy.business.domain.models.SalesOrder
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

sealed class SalesOrderListEvents {

    object SearchOrders : SalesOrderListEvents()

    data class SearchWithQuery(val query: String) : SalesOrderListEvents()

    object NextPage: SalesOrderListEvents()

    data class SalesCompleted(val order : SalesOrder) : SalesOrderListEvents()

    data class UpdateQuery(val query: String): SalesOrderListEvents()




    data class DeleteOrder(val order : SalesOrder): SalesOrderListEvents()


    object GetOrderAndFilter: SalesOrderListEvents()

    data class Error(val stateMessage: StateMessage): SalesOrderListEvents()

    object OnRemoveHeadFromQueue: SalesOrderListEvents()
}