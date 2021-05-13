package com.example.libproj2.models

class SearchSource(
        val searchSourceType: SearchSourceType,
        var enabled: Boolean
)

enum class SearchSourceType{
    LibraryBookInfo,

    LibraryBookCopy,

    Google
}