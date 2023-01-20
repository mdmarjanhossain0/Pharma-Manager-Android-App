package com.appbytes.pharma_manager.presentation.main.notification

import com.appbytes.pharma_manager.business.domain.models.Notification
import com.appbytes.pharma_manager.business.domain.util.Queue
import com.appbytes.pharma_manager.business.domain.util.StateMessage

data class NotificationState(
    val isLoading : Boolean = false,
    val notificationList : List<Notification> = listOf(),
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)