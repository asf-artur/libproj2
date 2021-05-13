package com.example.libproj2.repositories

class SettingsRepository {


    companion object{
        private var singleton: SettingsRepository? = null
        fun init(){
            if(singleton == null){
                singleton = SettingsRepository()
            }
        }

        fun get(): SettingsRepository{
            if(singleton != null){
                return singleton!!
            }
            else{
                throw ExceptionInInitializerError()
            }
        }
    }
}