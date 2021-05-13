package com.example.libproj2

import android.app.Application
import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.example.libproj2.di.AppComponent
import com.example.libproj2.di.ContextModule
import com.example.libproj2.di.DaggerAppComponent
import com.example.libproj2.repositories.*
import com.example.libproj2.services.BookNetWebAppService
import com.example.libproj2.utils.SystemServicesStorage
import com.google.gson.Gson
import okhttp3.Cookie
import java.lang.Exception

class MainApp : Application() {
    companion object{
        private lateinit var _component : AppComponent
        val component : AppComponent
        get() { return _component }
    }
    override fun onCreate() {
        super.onCreate()

        _component = DaggerAppComponent.builder()
            .contextModule(ContextModule(this))
            .build()

        val cookiesString = getSharedPreferences("default", Context.MODE_PRIVATE)
                .getString(SharedPreferencesRepository.UserIdentityCookiesKey, "")
        var cookies: MutableList<Cookie> = mutableListOf()
        try{
//            throw Exception()
            val dataItems = Gson().fromJson(cookiesString, DataItems::class.java)
            cookies = dataItems.cookies.toMutableList()
        }
        catch (e: Exception){
        }
        CookieRepository.init(cookies)
        BookCopiesRepository.init()
        BookNetWebAppService.init()

        SettingsRepository.init()
        BooksOnHandRepository.init()
        BooksHistoryRepository.init()
        BookSearchResultRepository.init()
//        UserRepository.init(this)
        BookInfoRepository.init()
        OldSearchResultRepository.init()
//        InAppNotificationsRepository.init()
        SharedPreferencesRepository.init(packageName)
        AllBookCopiesRepository.init()
        AllBookInfoRepository.init()
        AllUsersRepository.init()
        ReaderBookOnHandsRepository.init()


        val inputMethodManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        SystemServicesStorage.init(inputMethodManager)
    }
}