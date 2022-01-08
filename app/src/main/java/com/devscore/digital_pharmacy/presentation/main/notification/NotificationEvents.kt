package com.devscore.digital_pharmacy.presentation.main.notification

import com.devscore.digital_pharmacy.business.domain.util.StateMessage

sealed class NotificationEvents {

    object NewNotificationSearch : NotificationEvents()

    data class SearchWithQuery(val query: String) : NotificationEvents()

    object NextPage: NotificationEvents()

    data class UpdateQuery(val query: String): NotificationEvents()



    object GetOrderAndFilter: NotificationEvents()

    data class Error(val stateMessage: StateMessage): NotificationEvents()

    object OnRemoveHeadFromQueue: NotificationEvents()
}