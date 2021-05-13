package com.example.libproj2.api

import com.example.libproj2.models.User
import com.example.libproj2.retrofit.contracts.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface WebAppApi {
    @GET("user/GetById/{id}")
    fun getUserById(@Path("id") id : Int) : Single<UserPojo>

    @FormUrlEncoded
    @POST("user/Find")
    fun findUser(@Field("searchTerm") searchTerm : String) : Observable<List<UserPojo>>

    @FormUrlEncoded
    @POST("user/FindByBarcode")
    fun findUserByBarcode(@Field("barcode") barcode : String) : Single<UserPojo>

    @GET("user/GetAll")
    fun getAllUsers() : Observable<List<User>>

    @FormUrlEncoded
    @POST("user/SendClientToken")
    fun sendClientToken(@Field("clientToken") clientToken : String) : Completable

    @GET("book/GetBookCopy")
    fun getBookCopyById(@Query("bookCopyId") bookCopyId : Int): Single<BookCopyPojo>

    @GET("book/GetBooksOnHands/{readerUserId}")
    fun getBooksOnHands(@Path("readerUserId") readerUserId : Int): Observable<List<BookCopyPojo>>

    @GET("book/GetBooksOnHands")
    fun getBooksOnHands(): Observable<List<BookCopyPojo>>

    @GET("book/GetBooksHistory")
    fun getBooksHistory(): Observable<List<BookInfoPojo>>

    @GET("book/SearchByBarcode")
    fun searchBookCopyByBarcode(@Query("barcode") barcode: String) : Single<BookCopyPojo>

    @GET("book/SearchByRfid")
    fun searchBookCopyByRfid(@Query("rfid") rfid: String) : Single<BookCopyPojo>

    @GET("book/searchBookCopy")
    fun searchBookCopy(@Query("searchTerm") searchTerm: String) : Observable<List<BookCopyPojo>>

    @GET("book/GetAllBookInfo")
    fun get_all_bookinfo(): Call<List<BookInfoPojo>>

    @GET("book/GetAllBookInfo")
    fun get_all_bookinfoRx(): Observable<List<BookInfoPojo>>

    @GET("book/searchBookInfo")
    fun search_bookinfoRx(@Query("searchTerm") searchTerm : String): Observable<List<BookInfoPojo>>

    @Streaming
    @GET("book/Images/{imagePath}")
    fun get_bookinfo_image_Rx(@Path("imagePath") imagePath: String): Single<ResponseBody>

    @GET("book/GetAllBookCopy")
    fun get_all_bookcopy(): Call<List<BookCopyPojo>>

    @GET("book/GetAllBookCopy")
    fun get_all_bookcopyRx(): Observable<List<BookCopyPojo>>

    @FormUrlEncoded
    @POST("user/Login")
    fun login(@Field("login") login: String, @Field("password") password: String): Call<UserPojo>

    @FormUrlEncoded
    @POST("user/Login")
    fun loginRx(@Field("login") login: String, @Field("password") password: String): Single<UserPojo>

    @POST("user/Logout")
    fun logout(): Call<Unit>

    @POST("user/Logout")
    fun logoutRx(): Single<Unit>

    @POST("user/CheckLogin")
    fun checkLogin(): Call<UserPojo>

    @POST("user/CheckLogin")
    fun checkLoginRx(): Single<UserPojo>

    @FormUrlEncoded
    @POST("book/TryBorrowBook")
    fun borrow_try(@Field("bookId") bookId: Int, @Field("userId") userId: Int): Call<Unit>

    @FormUrlEncoded
    @POST("book/CompleteBorrowBook")
    fun completeBorrow(
        @Field("bookId") bookId: Int,
        @Field("userReaderId") userReaderId: Int,
        @Field("userLibrarianId") userLibrarianId: Int
    ): Call<Unit>

    @FormUrlEncoded
    @POST("book/CompleteBorrowBook")
    fun completeBorrowRx(
            @Field("bookId") bookId: Int,
            @Field("userReaderId") userReaderId: Int,
            @Field("userLibrarianId") userLibrarianId: Int
    ): Completable

    @FormUrlEncoded
    @POST("book/returnBook")
    fun returnBook(
        @Field("bookId") bookId: Int,
        @Field("userReaderId") userReaderId: Int,
        @Field("userLibrarianId") userLibrarianId: Int
    ): Call<Unit>

    @FormUrlEncoded
    @POST("book/returnBook")
    fun returnBookRx(
            @Field("bookId") bookId: Int,
            @Field("userReaderId") userReaderId: Int,
            @Field("userLibrarianId") userLibrarianId: Int
    ): Completable
}