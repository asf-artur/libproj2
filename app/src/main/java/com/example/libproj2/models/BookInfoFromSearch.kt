package com.example.libproj2.models

import java.util.*

class BookInfoFromSearch(val id: Int,
                         val bookInfoId: Int?,
                         val bookCopyId: Int?,
                         val title: String?,
                         val subTitle: String?,
                         val authors: List<String>?,
                         val publishedDate: Calendar?,
                         val pageCount: Int?,
                         val categories: List<String>?,
                         val language: String?,
                         val mainCategory: String?,
                         val industryIdentifiers: List<IndustryIdentifier>?,
                         val externalImagePath: String?,
                         val imagePath: String?,
                         val barcode: String?,
                         val bookInfoSource: BookInfoSource) {
}