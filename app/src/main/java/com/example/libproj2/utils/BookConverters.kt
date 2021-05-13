package com.example.libproj2.utils

import com.example.libproj2.models.BookCopy
import com.example.libproj2.models.BookInfo
import com.example.libproj2.models.BookInfoFromSearch
import com.example.libproj2.models.BookInfoSource
import com.example.libproj2.retrofit.contracts.BookCopyPojo
import com.example.libproj2.retrofit.contracts.BookInfoPojo
import com.example.libproj2.retrofit.contracts.toBookInfo


fun BookCopyPojo.toBookCopy() : BookCopy{
    return BookCopy(
            this.id,
            this.bookInfo.toBookInfo(),
            this.bookStatus,
            this.barcode,
            this.rfid,
            this.userId,
            this.returnDate.toCalendar(),
    )
}

fun BookCopyPojo.toBookInfoFromSearch(nextId: Int) : BookInfoFromSearch{
    return BookInfoFromSearch(
            nextId,
            this.bookInfo.id,
            this.id,
            this.bookInfo.title,
            this.bookInfo.subTitle,
            this.bookInfo.authors,
            this.bookInfo.publishedDate.toCalendar(),
            this.bookInfo.pageCount,
            this.bookInfo.categories,
            this.bookInfo.language,
            this.bookInfo.mainCategory,
            this.bookInfo.industryIdentifiers,
            this.bookInfo.imagePath,
            this.bookInfo.imagePath,
            this.bookInfo.barcode,
            BookInfoSource.LocalCatalogBookCopy
    )
}