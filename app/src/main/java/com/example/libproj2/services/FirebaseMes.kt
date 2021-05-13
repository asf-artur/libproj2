package com.example.libproj2.services

import android.util.Log
import com.example.libproj2.MainApp
import com.example.libproj2.models.FirebaseDataDm
import com.example.libproj2.models.FirebaseNotificationType
import com.example.libproj2.repositories.BooksHistoryRepository
import com.example.libproj2.repositories.BooksOnHandRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import javax.inject.Inject

class FirebaseMes : FirebaseMessagingService() {
    @Inject lateinit var bookActionNotificationService : BookActionNotificationService

    override fun onCreate() {
        super.onCreate()
        MainApp.component.inject(this)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        Log.d("MYTAG", "received: ${p0.notification?.title}")
        val data = p0.data
        val notification = p0.notification
        if(data.isNotEmpty()){
            val jsonData = data.get("data")
            val firebaseDataDm = Gson().fromJson(jsonData, FirebaseDataDm::class.java)
            val firebaseDm = firebaseDataDm.Data
            when(firebaseDm.notificationType){
                FirebaseNotificationType.TryBookBorrow -> bookActionNotificationService.createTryBorrowNotification(firebaseDm)
                FirebaseNotificationType.BookIsBorrowed ->{
                    BooksOnHandRepository.get().load()
                    BooksHistoryRepository.get().load()
                }
                FirebaseNotificationType.BookReturned ->{
                    BooksOnHandRepository.get().load()
                }
                else -> Log.d("MYTAG","ahahha, no")
            }
        }
        else if(notification != null){
            bookActionNotificationService.createNewsNotification(notification.title ?: "уведомление", notification.body ?: "")
        }

        val f = 0
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        Log.d("MYTAG", "deleted")
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        // TODO: здесь отправлять на сервер
        Log.d("MYTAG", "onnewtoken: $p0")
    }
}