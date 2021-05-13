package com.example.libproj2.repositories

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.libproj2.models.BookInfo
import com.example.libproj2.models.User
import com.example.libproj2.services.BookNetWebAppService
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable

class AllUsersRepository private constructor(){
    val users : MutableList<User> = mutableListOf()
    val usersLiveData : MutableLiveData<List<User>> = MutableLiveData()

    private val bookNetWebAppService = BookNetWebAppService.get()

    fun load(){
        val re = bookNetWebAppService.getAllUsers()
        val observer = object : Observer<List<User>> {
            override fun onSubscribe(d: Disposable?) {
//                TODO("Not yet implemented")
            }

            override fun onNext(t: List<User>?) {
                users.clear()
                users.addAll(t!!)
                usersLiveData.postValue(users)
            }

            override fun onError(e: Throwable?) {
//                TODO("Not yet implemented")
            }

            override fun onComplete() {
//                TODO("Not yet implemented")
            }

        }
        re.subscribe(observer)
    }


    companion object{
        private var singleton: AllUsersRepository? = null
        fun init(){
            if(singleton == null){
                singleton = AllUsersRepository()
            }
        }

        fun get(): AllUsersRepository{
            if(singleton != null){
                return singleton!!
            }
            else{
                throw ExceptionInInitializerError()
            }
        }
    }
}