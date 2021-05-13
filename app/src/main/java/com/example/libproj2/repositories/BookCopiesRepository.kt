package com.example.libproj2.repositories

import com.example.libproj2.models.BookCopy
import com.example.libproj2.models.BookInfo
import com.example.libproj2.services.BookNetWebAppService
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer

class BookCopiesRepository private constructor(){
    private val _books: MutableList<BookCopy> = mutableListOf()
    val books: List<BookCopy>
        get() { return _books.toList() }

    fun add(bookCopy: BookCopy){
        _books.add(bookCopy)
    }

    fun remove(bookCopy: BookCopy){
        _books.remove(bookCopy)
    }

    fun removeAll(){
        _books.clear()
    }

    fun tryRefresh(bookCopy: BookCopy, bookNetWebAppService: BookNetWebAppService){
        if(_books.firstOrNull { c -> c.id == bookCopy.id } != null){
            refresh(bookCopy, bookNetWebAppService)
        }
    }

    private fun refresh(bookCopy: BookCopy, bookNetWebAppService: BookNetWebAppService){
        var observer = object : SingleObserver<BookCopy>{
            override fun onSubscribe(d: Disposable?) {
//                TODO("Not yet implemented")
            }

            override fun onSuccess(t: BookCopy?) {
//                TODO("Not yet implemented")
                t?.let{
                    val bookCopyOld = _books.first { c -> c.id == bookCopy.id }
                    _books.remove(bookCopyOld)
                    _books.add(it)
                }
            }

            override fun onError(e: Throwable?) {
//                TODO("Not yet implemented")
            }

        }

        val request = bookNetWebAppService.getBookCopyById(bookCopy.id)
        request.subscribe(observer)
    }

    companion object{
        private var singleton: BookCopiesRepository? = null
        fun init(){
            if(singleton == null){
                singleton = BookCopiesRepository()
            }
        }

        fun get(): BookCopiesRepository{
            if(singleton != null){
                return singleton!!
            }
            else{
                throw ExceptionInInitializerError()
            }
        }
    }
}