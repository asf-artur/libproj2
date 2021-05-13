package com.example.libproj2.api

import com.example.libproj2.retrofit.contracts.GoogleBooksResponse
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface GoogleBooksApi {
    @GET("/books/v1/volumes")
    fun getAllSearchResults(@Query("q") searchTerm: String): Call<GoogleBooksResponse>

    @GET("/books/v1/volumes")
    fun getAllSearchResultsRx(@Query("q") searchTerm: String): Single<GoogleBooksResponse>

    @GET
    fun getBookImage(@Url url: String) : Single<ResponseBody>
}