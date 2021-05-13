package com.example.libproj2.repositories

import com.example.libproj2.models.BookInfoFromSearch

class BookSearchResultRepository private constructor(){
    private val _books: MutableList<BookInfoFromSearch> = mutableListOf()
    val books: List<BookInfoFromSearch>
        get() { return _books.toList() }

    fun add(bookInfo: BookInfoFromSearch){
        _books.add(bookInfo)
    }

    fun remove(bookInfo: BookInfoFromSearch){
        _books.remove(bookInfo)
    }

    fun clear(){
        _books.clear()
    }

    companion object{
        private var singleton: BookSearchResultRepository? = null
        fun init(){
            if(singleton == null){
                singleton = BookSearchResultRepository()
            }
        }

        fun get(): BookSearchResultRepository{
            if(singleton != null){
                return singleton!!
            }
            else{
                throw ExceptionInInitializerError()
            }
        }
    }
}