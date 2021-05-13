package com.example.libproj2.repositories

import com.google.gson.Gson
import okhttp3.Cookie

class CookieRepository private constructor(var cookies: MutableList<Cookie>){

    fun toJsonString() : String{
        val dataItem = DataItems(cookies)
        val data = Gson().toJson(dataItem)

        return data
    }

    companion object{
        private var singleton: CookieRepository? = null
        fun init(cookies: MutableList<Cookie>){
            if(singleton == null){
                singleton = CookieRepository(cookies)
            }
        }

        fun get(): CookieRepository{
            if(singleton != null){
                return singleton!!
            }
            else{
                throw ExceptionInInitializerError()
            }
        }
    }
}

class DataItems(
        val cookies: List<Cookie>
){}