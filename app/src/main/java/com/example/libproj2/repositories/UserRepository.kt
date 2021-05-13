package com.example.libproj2.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.example.libproj2.db.LibAppDatabase
import com.example.libproj2.models.User
import com.example.libproj2.models.UserCategory
import com.example.libproj2.services.ExampleBuilder
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.Executors

private const val DATABASE_NAME = "libapp-database"
class UserRepository constructor(context: Context){
    private val database : LibAppDatabase = Room.databaseBuilder(
        context.applicationContext,
        LibAppDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val exec = Executors.newSingleThreadExecutor()

    private val userDao = database.userDao()

    private var _user: User

    val user: User
    get() { return _user }

    val userLiveData: MutableLiveData<User> = MutableLiveData(null)

    init {
        val exampleBuilder = ExampleBuilder()
        _user = exampleBuilder.buildMe(UserCategory.Admin)
    }

    fun get(): LiveData<User>{
        return userDao.get()
    }

    fun getRx(): Single<User>{
        return userDao.getRx()
    }

    fun add(user: User){
        exec.execute {
            userDao.deleteAll()
            userDao.add(user)
            userLiveData.postValue(user)
        }

        BooksOnHandRepository.get().load()
        BooksHistoryRepository.get().load()
    }

    fun tryAdd(user: User, alreadyHere: Boolean){
        if(!alreadyHere){
            exec.execute {
                userDao.deleteAll()
                userDao.add(user)
            }
        }
        userLiveData.postValue(user)
    }

    fun logout(){
        exec.execute {
            userDao.deleteAll()
            userLiveData.postValue(null)
        }
    }


    companion object{
        private var singleton: UserRepository? = null
        fun init(context: Context){
            if(singleton == null){
                singleton = UserRepository(context)
            }
        }

        fun get(): UserRepository{
            if(singleton != null){
                return singleton!!
            }
            else{
                throw ExceptionInInitializerError()
            }
        }
    }
}