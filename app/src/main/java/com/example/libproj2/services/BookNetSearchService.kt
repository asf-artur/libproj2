package com.example.libproj2.services

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.libproj2.api.GoogleBooksApi
import com.example.libproj2.api.RslApi
import com.example.libproj2.models.BookInfo
import com.example.libproj2.repositories.BookInfoRepository
import com.example.libproj2.retrofit.contracts.BookPojo
import com.example.libproj2.retrofit.contracts.GoogleBooksResponse
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

class BookNetSearchService {
    private val bookInfoRepository = BookInfoRepository.get()

    fun searchInGoogleBooksRx(searchTerm: String) : Single<GoogleBooksResponse>{
        val retrofit = getRetrofit()
        val rslApi = retrofit.create(GoogleBooksApi::class.java)
        val request = rslApi.getAllSearchResultsRx(searchTerm)

        return request
    }

    fun searchInGoogleBooks(searchTerm: String) : MutableLiveData<MutableList<BookInfo>>{
        val retrofit = getRetrofit()
        val rslApi = retrofit.create(GoogleBooksApi::class.java)
        val request = rslApi.getAllSearchResults(searchTerm)
        val result: MutableLiveData<MutableList<BookInfo>> = MutableLiveData()
        result.value = mutableListOf()
        var books: MutableList<BookInfo> = mutableListOf()
        Log.d("MYTAG", "started req")
        try {
            request.enqueue(object : Callback<GoogleBooksResponse> {
                override fun onResponse(
                        call: Call<GoogleBooksResponse>,
                        response: Response<GoogleBooksResponse>
                ) {
                    val res = response.body()
                    res?.let{
                        val booksg = res.items
                        books = booksg.map {
                            val author = if(it.volumeInfo.authors?.size != 0 && it.volumeInfo.authors != null)
                                it.volumeInfo.authors[0]
                            else
                                "noauthor"
                            val newId = bookInfoRepository.books.size
                            return@map BookInfo(
                                    newId,
                                    it.volumeInfo.title,
                                    it.volumeInfo.subtitle ?: "",
                                    it.volumeInfo.authors ?: listOf(),
                                    Calendar.getInstance(),
                                    it.volumeInfo.pageCount,
                                    it.volumeInfo.categories,
                                    it.volumeInfo.language,
                                    it.volumeInfo.mainCategory,
                                    listOf(),
                                    null,
                                    null
                            )
                        }.toMutableList()

                        books.forEach {
                            bookInfoRepository.add(it )
                        }

                        result.postValue(books)
                    }


                    val q = 0
                }

                override fun onFailure(call: Call<GoogleBooksResponse>, t: Throwable) {
                    throw t
                }

            })
        }
        catch (e: Exception){
            Log.d("MYTAG", "exc")
        }

        return result
    }

    fun search(searchTerm: String) : MutableLiveData<MutableList<BookInfo>>{
        val retrofit = getRetrofit()
        val rslApi = retrofit.create(RslApi::class.java)
        val request = rslApi.getAllSearchResults()
        val result: MutableLiveData<MutableList<BookInfo>> = MutableLiveData()
        result.value = mutableListOf()
        val books: MutableList<BookInfo> = mutableListOf()
        Log.d("MYTAG", "started req")
        try {
            request.enqueue(object : Callback<List<BookPojo>> {
                override fun onResponse(
                        call: Call<List<BookPojo>>,
                        response: Response<List<BookPojo>>
                ) {
                    response.body()?.forEach {
                        val book = getBookInfoFromBookPojo(it)
                        books.add(book)
                    }

                    result.postValue(books)
                }

                override fun onFailure(call: Call<List<BookPojo>>, t: Throwable) {
                    throw t
                }

            })
        }
        catch (e: Exception){
            Log.d("MYTAG", "exc")
        }

        return result
    }

    private fun getBookInfoFromBookPojo(bookPojo: BookPojo): BookInfo{
        val newId = bookInfoRepository.books.size
        val result = BookInfo(
                newId,
                bookPojo.title ?:"название не написали..",
                "",
                listOf(bookPojo.author ?:"автора не написали..."),
                Calendar.getInstance(),
                0,
                listOf(),
                "",
                "",
                listOf(),
                null,
                null
        )

        bookInfoRepository.add(result)
        return result
    }

    private fun getRetrofit(): Retrofit{
        val httpClient = OkHttpClient.Builder()
                .callTimeout(20, TimeUnit.MINUTES)
                .connectTimeout(20, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.MINUTES)
                .writeTimeout(30, TimeUnit.MINUTES)

        val url = "https://www.googleapis.com/"
        val url1 = "http://192.168.31.21:44331/"
        val retrofit: Retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
                .build()

        return retrofit
    }
}