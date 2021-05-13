package com.example.libproj2.repositories

import android.app.Activity
import android.content.Context
import okhttp3.Cookie

class SharedPreferencesRepository private constructor(private val packageName: String){

    fun getSharedPreference(activity: Activity, key: String) : String{
        return activity.getPreferences(Context.MODE_PRIVATE)
                ?.getString(packageName + key, "") ?: ""
    }

    fun setSharedPreference(activity: Activity, key: String, data: String){
        activity.getPreferences(Context.MODE_PRIVATE)
                .edit()
                .putString(packageName + key, data)
                .apply()
    }

    /*
    Для ключей для сохранения настроек
     */
    companion object{
        val UserIdentityCookiesKey = "UserIdentityCookiesKey"

        private var singleton: SharedPreferencesRepository? = null
        fun init(packageName: String){
            if(singleton == null){
                singleton = SharedPreferencesRepository(packageName + ".")
            }
        }

        fun get(): SharedPreferencesRepository{
            if(singleton != null){
                return singleton!!
            }
            else{
                throw ExceptionInInitializerError()
            }
        }
    }
}