package com.example.libproj2.repositories

import com.example.libproj2.models.BookCopy
import com.example.libproj2.models.BookInfo

class OldSearchResultRepository {
    private val _books: MutableList<BookInfo> = mutableListOf()
    val books: List<BookInfo>
        get() { return _books.toList() }

    fun add(bookInfo: BookInfo){
        _books.add(bookInfo)
    }

    fun remove(bookInfo: BookInfo){
        _books.remove(bookInfo)
    }

    fun clear(){
        _books.clear()
    }


    companion object{
        private var singleton: OldSearchResultRepository? = null
        fun init(){
            if(singleton == null){
                singleton = OldSearchResultRepository()
            }
        }

        fun get(): OldSearchResultRepository{
            if(singleton != null){
                return singleton!!
            }
            else{
                throw ExceptionInInitializerError()
            }
        }
    }
}