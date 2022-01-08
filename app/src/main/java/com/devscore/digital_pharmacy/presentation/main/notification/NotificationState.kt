package com.devscore.digital_pharmacy.presentation.main.notification

import com.devscore.digital_pharmacy.business.domain.models.Notification
import com.devscore.digital_pharmacy.business.domain.models.Report
import com.devscore.digital_pharmacy.business.domain.util.Queue
import com.devscore.digital_pharmacy.business.domain.util.StateMessage

data class NotificationState(
    val isLoading : Boolean = false,
    val notificationList : List<Notification> = listOf(),
    val page: Int = 1,
    val isQueryExhausted: Boolean = false, // no more results available, prevent next page
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)