package com.example.libproj2.utils

import android.view.inputmethod.InputMethodManager
import com.example.libproj2.repositories.UserRepository

class SystemServicesStorage private constructor(val inputMethodManager: InputMethodManager) {

    companion object{
        private var singleton: SystemServicesStorage? = null
        fun init(inputMethodManager: InputMethodManager){
            if(singleton == null){
                singleton = SystemServicesStorage(inputMethodManager)
            }
        }

        fun get(): SystemServicesStorage {
            if(singleton != null){
                return singleton!!
            }
            else{
                throw ExceptionInInitializerError()
            }
        }
    }
}