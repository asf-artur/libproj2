package com.example.libproj2.models

import com.example.libproj2.utils.toDateString
import java.util.*

open class BookInfo(
        val id: Int,
        val title: String?,
        val subTitle: String?,
        val authors: List<String>?,
        val publishedDate: Calendar?,
        val pageCount: Int?,
        val categories: List<String>?,
        val language: String?,
        val mainCategory: String?,
        val industryIdentifiers: List<IndustryIdentifier>?,
        val imagePath: String?,
        val barcode: String?,
) {
}

fun BookInfo.toText(): String{
    val dict: MutableMap<String, String> = mutableMapOf()
    subTitle?.let {
        dict["Подзаголовок"] = subTitle
    }
    authors?.let {
        dict["Авторы"] = authors.joinToString()
    }
    publishedDate?.let {
        dict["Дата публикации"] = publishedDate.toDateString()
    }
    pageCount?.let {
        dict["Количество страниц"] = pageCount.toString()
    }
    categories?.let {
        dict["Категории"] = categories.joinToString()
    }

    return dict.map {
        return@map "${it.key}: ${it.value}"
    }.joinToString("\n")
}