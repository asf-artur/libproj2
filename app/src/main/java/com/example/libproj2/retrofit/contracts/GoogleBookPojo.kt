package com.example.libproj2.retrofit.contracts

import com.example.libproj2.models.BookInfo
import com.example.libproj2.models.BookInfoFromSearch
import com.example.libproj2.models.BookInfoSource
import java.util.*

data class GoogleBooksResponse(
        val items: List<GoogleBookPojo>
)

data class GoogleBookPojo(
        val id: String,
        val volumeInfo: VolumeInfo,
)

data class VolumeInfo(
        val title: String,
        val subtitle: String?,
        val authors: List<String>?,
        val publisher: String,
        val publishedDate: String,
        val description: String,
        val pageCount: Int, // TODO: проверить
        val categories: List<String>,
        val imageLinks: ImageLinks?,
        val language: String,
        val mainCategory: String,
)

data class ImageLinks(
        val thumbnail: String?,
        val large: String?,
        val extraLarge: String?,
)

fun GoogleBooksResponse.toBookInfoList(firstId : Int) : List<BookInfo>{
    val googleBookPojos = this.items
    var newId = firstId - 1
    val result = googleBookPojos.map {
        newId++
        return@map it.toBookInfo(newId)
    }

    return result
}

fun GoogleBookPojo.toBookInfo(newId : Int) : BookInfo{
    return BookInfo(
            newId,
            this.volumeInfo.title,
            this.volumeInfo.subtitle ?: "",
            this.volumeInfo.authors ?: listOf(),
            Calendar.getInstance(),
            this.volumeInfo.pageCount,
            this.volumeInfo.categories,
            this.volumeInfo.language,
            this.volumeInfo.mainCategory,
            listOf(),
            null,
            null
    )
}

fun GoogleBookPojo.toBookInfoFromSearch(newId : Int) : BookInfoFromSearch{
    val extImagePath = this.volumeInfo.imageLinks?.large ?: this.volumeInfo.imageLinks?.thumbnail
    val imagePath = if(extImagePath != null) "from_google" + this.id + ".jpg" else null
    return BookInfoFromSearch(
            newId,
            null,
            null,
            this.volumeInfo.title,
            this.volumeInfo.subtitle ?: "",
            this.volumeInfo.authors ?: listOf(),
            Calendar.getInstance(),
            this.volumeInfo.pageCount,
            this.volumeInfo.categories,
            this.volumeInfo.language,
            this.volumeInfo.mainCategory,
            listOf(),
            extImagePath,
            imagePath,
            null,
            BookInfoSource.Google
    )
}