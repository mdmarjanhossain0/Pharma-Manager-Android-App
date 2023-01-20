package com.appbytes.pharma_manager.presentation.main.notification

import com.appbytes.pharma_manager.business.domain.util.StateMessage

sealed class NotificationEvents {

    object NewNotificationSearch : NotificationEvents()

    data class SearchWithQuery(val query: String) : NotificationEvents()

    object NextPage: NotificationEvents()

    data class UpdateQuery(val query: String): NotificationEvents()



    object GetOrderAndFilter: NotificationEvents()

    data class Error(val stateMessage: StateMessage): NotificationEvents()

    object OnRemoveHeadFromQueue: NotificationEvents()
}