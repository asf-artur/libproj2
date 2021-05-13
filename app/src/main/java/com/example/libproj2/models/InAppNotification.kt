package com.example.libproj2.models

import java.util.*

open class InAppNotification(
    val id: Int,
    val title: String,
    val content: String,
    val date: Calendar,
    var inAppNotificationType: InAppNotificationType,
) {
    var isRead = false
}