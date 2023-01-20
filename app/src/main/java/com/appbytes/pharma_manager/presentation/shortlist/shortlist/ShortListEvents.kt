package com.appbytes.pharma_manager.presentation.shortlist.shortlist

import com.appbytes.pharma_manager.business.domain.models.ShortList
import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class ShortListEvents {

    object NewShortListSearch : ShortListEvents()

    data class SearchWithQuery(val query: String) : ShortListEvents()

    object NextPage: ShortListEvents()

    data class UpdateQuery(val query: String): ShortListEvents()

    data class UpdateFilter(val filter : String) : ShortListEvents()

    data class DeleteShortList(val shortList : ShortList): ShortListEvents()


    object GetOrderAndFilter: ShortListEvents()

    data class Error(val stateMessage: StateMessage): ShortListEvents()

    object OnRemoveHeadFromQueue: ShortListEvents()
}