package com.devscore.digital_pharmacy.presentation.shortlist.shortlist

import com.devscore.digital_pharmacy.business.domain.models.LocalMedicine
import com.devscore.digital_pharmacy.business.domain.models.ShortList
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

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