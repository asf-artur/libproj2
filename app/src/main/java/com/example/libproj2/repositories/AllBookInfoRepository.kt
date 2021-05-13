package com.example.libproj2.repositories

import androidx.lifecycle.MutableLiveData
import com.example.libproj2.models.BookInfo
import com.example.libproj2.services.BookNetWebAppService
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer

class AllBookInfoRepository private constructor(){
    private val bookNetWebAppService = BookNetWebAppService.get()
    private val _books: MutableList<BookInfo> = mutableListOf()
    val books: List<BookInfo>
        get() { return _books.toList() }
    val booksLiveData: MutableLiveData<List<BookInfo>> = MutableLiveData()

    fun add(bookInfo: BookInfo){
        _books.add(bookInfo)
    }

    fun remove(bookInfo: BookInfo){
        _books.remove(bookInfo)
    }

    fun load(){
        val re = bookNetWebAppService.getAllBookInfoRx()
        val onNext = Consumer<List<BookInfo>>{
            _books.addAll(it)
            booksLiveData.postValue(_books)
        }
        val observer = object : Observer<List<BookInfo>> {
            override fun onSubscribe(d: Disposable?) {
//                TODO("Not yet implemented")
            }

            override fun onNext(t: List<BookInfo>?) {
                _books.clear()
                _books.addAll(t!!)
                booksLiveData.postValue(_books)
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
        private var singleton: AllBookInfoRepository? = null
        fun init(){
            if(singleton == null){
                singleton = AllBookInfoRepository()
            }
        }

        fun get(): AllBookInfoRepository{
            if(singleton != null){
                return singleton!!
            }
            else{
                throw ExceptionInInitializerError()
            }
        }
    }
}