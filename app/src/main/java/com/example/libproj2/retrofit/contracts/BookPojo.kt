package com.example.libproj2.retrofit.contracts

data class BookPojo(
    val author: String?,
    val title : String?,
    val publishersImprint : String?,
    val physicalDescription : String?,
    val volume : String?,
    val isbn : String?,
    val tags: List<String>?,
) {
}