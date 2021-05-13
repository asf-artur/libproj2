package com.example.libproj2.repositories

import android.util.Log
import com.example.libproj2.models.User
import com.example.libproj2.services.BookNetWebAppService
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable

class UserFromNotificationRepository {
    val users : MutableList<User> = mutableListOf()
    private val bookNetWebAppService = BookNetWebAppService.get()

    fun addUser(id : Int): Single<User> {
        val observer = object : SingleObserver<User>{
            override fun onSubscribe(d: Disposable?) {
            }

            override fun onSuccess(t: User?) {
                t?.let{
                    users.add(it)
                }
            }

            override fun onError(e: Throwable?) {
                Log.d("MYTAG", "${e!!.message}")
            }

        }

        val response = bookNetWebAppService.getUserById(id)
//        response.subscribe(observer)

        return response
    }
}