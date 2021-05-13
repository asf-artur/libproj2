package com.example.libproj2.retrofit.contracts

import com.example.libproj2.models.BookInfo
import com.example.libproj2.models.BookStatus
import java.util.*

data class BookCopyPojo(
        val id: Int,
        val bookInfo: BookInfoPojo,
        val bookStatus: BookStatus,
        val barcode: String?,
        val rfid: String?,
        val userId: Int?,
        val returnDate: Date?
)