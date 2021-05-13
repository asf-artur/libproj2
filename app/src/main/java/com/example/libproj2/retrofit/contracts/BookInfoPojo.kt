package com.example.libproj2.retrofit.contracts

import com.example.libproj2.models.BookInfo
import com.example.libproj2.models.BookInfoFromSearch
import com.example.libproj2.models.BookInfoSource
import com.example.libproj2.models.IndustryIdentifier
import com.example.libproj2.utils.toCalendar
import java.util.*

data class BookInfoPojo(
        val id: Int,
        val title: String,
        val subTitle: String,
        val authors: List<String>,
        val publishedDate: Date?,
        val pageCount: Int,
        val categories: List<String>,
        val language: String,
        val mainCategory: String,
        val industryIdentifiers: List<IndustryIdentifier>,
        val imagePath: String?,
        val barcode: String?,
) {
}

fun BookInfoPojo.toBookInfo() : BookInfo {
    return BookInfo(
            this.id,
            this.title,
            this.subTitle,
            this.authors,
            this.publishedDate.toCalendar(),
            this.pageCount,
            this.categories,
            this.language,
            this.mainCategory,
            this.industryIdentifiers,
            this.imagePath,
            this.barcode
    )
}


fun BookInfoPojo.toBookInfoFromSearch(nextId: Int) : BookInfoFromSearch {
    return BookInfoFromSearch(
            nextId,
            this.id,
            null,
            this.title,
            this.subTitle,
            this.authors,
            this.publishedDate.toCalendar(),
            this.pageCount,
            this.categories,
            this.language,
            this.mainCategory,
            this.industryIdentifiers,
            this.imagePath,
            this.imagePath,
            this.barcode,
            BookInfoSource.LocalCatalogBookInfo
    )
}