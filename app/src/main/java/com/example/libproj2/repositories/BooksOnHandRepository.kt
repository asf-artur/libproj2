package com.example.libproj2.repositories

import com.example.libproj2.models.BookCopy
import com.example.libproj2.services.BookNetWebAppService
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer

class BooksOnHandRepository private constructor(){
    private val bookNetWebAppService = BookNetWebAppService.get()
    private val _books: MutableList<BookCopy> = mutableListOf()
    val books: List<BookCopy>
        get() { return _books.toList() }

    fun load(){
        val re = bookNetWebAppService.getBooksOnHands()
        val onNext = Consumer<List<BookCopy>>{
            _books.addAll(it)
        }
        val observer = object : Observer<List<BookCopy>>{
            override fun onSubscribe(d: Disposable?) {
//                TODO("Not yet implemented")
            }

            override fun onNext(t: List<BookCopy>?) {
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

    fun add(bookCopy: BookCopy){
        _books.add(bookCopy)
    }

    fun remove(bookCopy: BookCopy){
        _books.remove(bookCopy)
    }

    companion object{
        private var singleton: BooksOnHandRepository? = null
        fun init(){
            if(singleton == null){
                singleton = BooksOnHandRepository()
            }
        }

        fun get(): BooksOnHandRepository{
            if(singleton != null){
                return singleton!!
            }
            else{
                throw ExceptionInInitializerError()
            }
        }
    }
}