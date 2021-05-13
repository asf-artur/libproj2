package com.example.libproj2.repositories

import com.example.libproj2.models.BookInfo

class BookInfoRepository private constructor(){
    private val _books: MutableList<BookInfo> = mutableListOf()
    val books: List<BookInfo>
        get() { return _books.toList() }

    fun add(bookInfo: BookInfo){
        _books.add(bookInfo)
    }

    fun remove(bookInfo: BookInfo){
        _books.remove(bookInfo)
    }

    companion object{
        private var singleton: BookInfoRepository? = null
        fun init(){
            if(singleton == null){
                singleton = BookInfoRepository()
            }
        }

        fun get(): BookInfoRepository{
            if(singleton != null){
                return singleton!!
            }
            else{
                throw ExceptionInInitializerError()
            }
        }
    }
}