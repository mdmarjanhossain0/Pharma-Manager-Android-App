package com.devscore.digital_pharmacy.presentation.main.dashboard

import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.SalesDetailsMonth
import com.devscore.digital_pharmacy.business.domain.models.ShortList
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

sealed class DashBoardEvents {

    object GetMonthSalesTotalReport : DashBoardEvents()



    object Sync : DashBoardEvents()

    data class SearchWithQuery(val query: String) : DashBoardEvents()

    object NextPage: DashBoardEvents()

    data class UpdateQuery(val query: String): DashBoardEvents()

    data class Local(val value: Int): DashBoardEvents()
    data class Sales(val value: Int): DashBoardEvents()
    data class Purchases(val value: Int): DashBoardEvents()
    data class Customer(val value: Int): DashBoardEvents()
    data class Supplier(val value: Int): DashBoardEvents()


    object GetOrderAndFilter: DashBoardEvents()

    data class Error(val stateMessage: StateMessage): DashBoardEvents()

    object OnRemoveHeadFromQueue: DashBoardEvents()
}