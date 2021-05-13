package com.example.libproj2.models

import java.lang.Exception

enum class BookInfoSource {
    LocalCatalogBookInfo,

    LocalCatalogBookCopy,

    Google
}

fun BookInfoSource.toRusString() : String{
    return when(this){
        BookInfoSource.LocalCatalogBookCopy -> "Экземпляр"
        BookInfoSource.LocalCatalogBookInfo -> "Каталог библиотеки"
        BookInfoSource.Google -> "Каталог Google"
        else -> throw Exception()
    }
}