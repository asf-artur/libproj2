package com.example.libproj2.api

import com.example.libproj2.retrofit.contracts.BookPojo
import retrofit2.Call
import retrofit2.http.*

interface RslApi {
    @GET("getall")
    fun getAllSearchResults(): Call<List<BookPojo>>

    @GET("searcha")
    fun searchRsl(@Query("searchTerm") searchTerm: String): Call<List<BookPojo>>

}