package com.example.libproj2.repositories

import androidx.lifecycle.MutableLiveData
import com.example.libproj2.models.BookCopy
import com.example.libproj2.services.BookNetWebAppService
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable

class ReaderBookOnHandsRepository private constructor(){
    private val bookNetWebAppService = BookNetWebAppService.get()
    private val _books: MutableList<BookCopy> = mutableListOf()
    val books: List<BookCopy>
        get() { return _books.toList() }

    val booksLiveData : MutableLiveData<List<BookCopy>> = MutableLiveData()

    fun load(userReaderId : Int){
        val re = bookNetWebAppService.getBooksOnHands(userReaderId)
        val observer = object : Observer<List<BookCopy>> {
            override fun onSubscribe(d: Disposable?) {
//                TODO("Not yet implemented")
            }

            override fun onNext(t: List<BookCopy>?) {
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

    fun add(bookCopy: BookCopy){
        _books.add(bookCopy)
    }

    fun remove(bookCopy: BookCopy){
        _books.remove(bookCopy)
    }

    companion object{
        private var singleton: ReaderBookOnHandsRepository? = null
        fun init(){
            if(singleton == null){
                singleton = ReaderBookOnHandsRepository()
            }
        }

        fun get(): ReaderBookOnHandsRepository{
            if(singleton != null){
                return singleton!!
            }
            else{
                throw ExceptionInInitializerError()
            }
        }
    }
}