package com.example.libproj2.services

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.SingleSubject
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import java.lang.Exception
import java.util.concurrent.Flow

class FirebaseService {
    fun getFirebaseToken() : PublishSubject<String>{
        val publishSubject = PublishSubject.create<String>()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MYTAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = token.toString()
            Log.d("MYTAG", msg)
            publishSubject.onNext(msg)
        })

        return publishSubject
    }
}