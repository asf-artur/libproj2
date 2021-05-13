package com.example.libproj2.repositories

import android.util.Log
import com.example.libproj2.models.BookCopy
import com.example.libproj2.models.BookInfo
import com.example.libproj2.services.BookNetWebAppService
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer

class BooksHistoryRepository private constructor(){
    private val bookNetWebAppService = BookNetWebAppService.get()
    private val _books: MutableList<BookInfo> = mutableListOf()
    val books: List<BookInfo>
    get() { return _books.toList() }

    init{
    }

    fun load(){
        val re = bookNetWebAppService.getBooksHistory()
        val onNext = Consumer<List<BookInfo>>{
            _books.addAll(it)
        }
        val observer = object : Observer<List<BookInfo>> {
            override fun onSubscribe(d: Disposable?) {
//                TODO("Not yet implemented")
            }

            override fun onNext(t: List<BookInfo>?) {
                _books.clear()
                _books.addAll(t!!)
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

    fun add(bookInfo: BookInfo){
        _books.add(bookInfo)
    }

    fun remove(bookInfo: BookInfo){
        _books.remove(bookInfo)
    }

    companion object{
        private var singleton: BooksHistoryRepository? = null
        fun init(){
            if(singleton == null){
                singleton = BooksHistoryRepository()
            }
        }

        fun get(): BooksHistoryRepository{
            if(singleton != null){
                return singleton!!
            }
            else{
                throw ExceptionInInitializerError()
            }
        }
    }
}