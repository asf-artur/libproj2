package com.example.libproj2.models

import java.util.*

class BookNotification(
    id: Int,
    title: String,
    val userId: Int,
    val bookCopyId: Int,
    date: Calendar,
    inAppNotificationType: InAppNotificationType
): InAppNotification(id, title, "", date, inAppNotificationType) {

}