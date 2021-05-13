package com.example.libproj2.models

import java.util.*

data class BookCopy(
        val id: Int,
        val bookInfo: BookInfo,
        val bookStatus: BookStatus,
        val barcode: String?,
        val rfidId: String?,
        val userId: Int?,
        val returnDate: Calendar?
) {
    val author:String get() { return bookInfo.authors?.get(0) ?: ""}

    val title:String get() { return bookInfo?.title ?: ""}
}