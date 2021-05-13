package com.example.libproj2.services

import com.example.libproj2.models.BookNotification
import com.example.libproj2.models.FirebaseDm
import com.example.libproj2.models.InAppNotification
import com.example.libproj2.models.InAppNotificationType
import com.example.libproj2.repositories.InAppNotificationsRepository
import com.google.android.material.snackbar.Snackbar
import java.util.*
import javax.inject.Inject

class BookActionNotificationService(val inAppNotificationsRepository: InAppNotificationsRepository) {

    fun createTryBorrowNotification(firebaseDm: FirebaseDm){
        val nextId = inAppNotificationsRepository.inAppNotifications.size
        val bookNotification = BookNotification(
                nextId,
                "Попытка взять книгу",
                firebaseDm.initiatorUserId,
                firebaseDm.bookCopyId,
                Calendar.getInstance(),
                InAppNotificationType.TryBorrowBook
        )

        inAppNotificationsRepository.add(bookNotification)
    }

    fun createNewsNotification(title: String, text : String){
        val nextId = inAppNotificationsRepository.inAppNotifications.size
        val inAppNotification = InAppNotification(nextId, title, text, Calendar.getInstance(), InAppNotificationType.Important)

        inAppNotificationsRepository.add(inAppNotification)
    }
}