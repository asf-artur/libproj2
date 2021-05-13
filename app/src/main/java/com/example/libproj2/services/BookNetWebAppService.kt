 package com.example.libproj2.services

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.libproj2.api.GoogleBooksApi
import com.example.libproj2.api.WebAppApi
import com.example.libproj2.models.*
import com.example.libproj2.repositories.*
import com.example.libproj2.retrofit.contracts.*
import com.example.libproj2.utils.toBookCopy
import com.example.libproj2.utils.toBookInfoFromSearch
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.*

private const val LOCALAPP_URL = "http://192.168.31.21:44331/api/"
private const val LOCALAPP_URL_ORACLE = "http://152.70.52.35:3244/api/"
private const val COOKIE_HEADER = "Set-Cookie"
class BookNetWebAppService private constructor(){
    private val bookCopiesRepository = BookCopiesRepository.get()
    val api_url = LOCALAPP_URL
    private lateinit var retrofit: Retrofit
    private lateinit var cookieString: String
    private var logined: Boolean = false
    private val cookieRepository = CookieRepository.get()
    init {
        gRetrofit()
    }

    fun getBookCopyById(id: Int): Single<BookCopy> {
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.getBookCopyById(id)

        return request
                .map {
                    it.toBookCopy()
                }
    }

    fun getUserById(id : Int) : Single<User> {
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.getUserById(id)

        return request
                .map {
                    return@map it.toUser()
                }
    }

    fun findUser(searchTerm: String) : Observable<List<User>> {
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.findUser(searchTerm)

        return request
                .map {
                    it.map {
                        it.toUser()
                    }
                }
    }

    fun findUserByBarcode(barcode: String) : Single<User> {
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.findUserByBarcode(barcode)

        return request
                .map {
                    it.toUser()
                }
    }

    fun getAllUsers() : Observable<List<User>> {
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.getAllUsers()

        return request
    }

    fun sendClientToken(clientToken: String) : Completable{
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.sendClientToken(clientToken)

        return request
    }

    fun searchBookCopy(searchTerm: String, nextId: Int) : Observable<List<BookInfoFromSearch>>{
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.searchBookCopy(searchTerm)

        var newId = nextId - 1

        bookCopiesRepository.removeAll()

        return request.map { it ->
            val result = it.map {
                newId++
                bookCopiesRepository.add(it.toBookCopy())
                it.toBookInfoFromSearch(newId)
            }
            return@map result
        }
    }

    fun searchBookCopyByRfid(rfid: String) : Single<BookCopy>{
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.searchBookCopyByRfid(rfid)

        return request.map {
            return@map it.toBookCopy()
        }
    }

    fun searchBookCopyByBarcode(barcode: String) : Single<BookCopy>{
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.searchBookCopyByBarcode(barcode)

        return request.map {
            return@map it.toBookCopy()
        }
    }

    fun getBooksHistory() : Observable<List<BookInfo>>{
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.getBooksHistory()

        return request
                .map {
                    val result = it.map {
                        it.toBookInfo()
                    }
                    return@map result
                }
    }

    fun getBooksOnHands() : Observable<List<BookCopy>>{
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.getBooksOnHands()

        return request
                .map {
                    val result = it.map {
                        it.toBookCopy()
                    }
                    return@map result
                }
    }

    fun getBooksOnHands(userReaderId: Int) : Observable<List<BookCopy>>{
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.getBooksOnHands(userReaderId)

        return request
                .map {
                    val result = it.map {
                        it.toBookCopy()
                    }
                    return@map result
                }
    }

    fun getAllBookCopyRx() : Observable<List<BookCopy>>{
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.get_all_bookcopyRx()

        return request
                .map {
                    val result = it.map {
                        it.toBookCopy()
                    }
                    return@map result
                }
    }

    fun getAllBookCopyCatalogRx(nextId: Int) : Observable<List<BookInfoFromSearch>>{
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.get_all_bookcopyRx()

        var newId = nextId - 1

        return request
                .map {
                    val result = it.map {
                        newId++
                        it.toBookInfoFromSearch(newId)
                    }
                    return@map result
                }
    }

    fun getAllBookCopy() : LiveData<List<BookCopy>>{
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.get_all_bookcopy()
        val result: MutableLiveData<List<BookCopy>> = MutableLiveData()
        Log.d("MYTAG", "started req")
        try {
            request.enqueue(object : Callback<List<BookCopyPojo>> {
                override fun onResponse(
                        call: Call<List<BookCopyPojo>>,
                        response: Response<List<BookCopyPojo>>
                ) {
                    if(response.code() <= 400){
                        val bookCopies = response.body()!!
                                .map {
                                    return@map it.toBookCopy()
                                }

                        result.postValue(bookCopies)

                        Log.d("MYTAG", "done")
                    }
                    else{
                    }
                }

                override fun onFailure(call: Call<List<BookCopyPojo>>, t: Throwable) {
                    Log.d("MYTAG", "failed")
//                    throw t
                }
            })
        }
        catch (e: Exception){
            Log.d("MYTAG", "exc")
        }

        return result
    }

    fun getAllBookInfoRx() : Observable<List<BookInfo>>{
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.get_all_bookinfoRx()

        return request
            .map {
                val result = it.map {
                    it.toBookInfo()
                }
                return@map result
            }
    }

    fun searchBookInfoRx(searchTerm : String, nextId: Int) : Observable<List<BookInfoFromSearch>>{
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.search_bookinfoRx(searchTerm)

        var newId = nextId - 1

        return request
                .map {
                    val result = it.map {
                        newId++
                        it.toBookInfoFromSearch(newId)
                    }
                    return@map result
                }
    }

    fun getBookInfoImageRx(imagePath : String, context: Context) : Single<ResponseBody>{
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.get_bookinfo_image_Rx(imagePath)

        val observer = object : SingleObserver<ResponseBody>{
            override fun onSubscribe(d: Disposable?) {
            }

            override fun onSuccess(t: ResponseBody?) {
                try{
//                    val bitmap = t!!.byteStream().use(BitmapFactory::decodeStream)
//                    val externalCacheFile = File(context.externalCacheDir, imagePath)
//                    val stream = FileOutputStream(externalCacheFile)
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//                    stream.flush()
//                    stream.close()
                    Log.d("MYTAG", "downloaded !!!!!!!")
                }
                catch (e : Exception){
                    Log.d("MYTAG", "download error EEEEEE")
                }
            }

            override fun onError(e: Throwable?) {
            }
        }

        return request
    }

    fun getGoogleBookInfoImageRx(imagePath : String) : Single<ResponseBody>{
        val api = retrofit.create(GoogleBooksApi::class.java)
        val request = api.getBookImage(imagePath)

        val observer = object : SingleObserver<ResponseBody>{
            override fun onSubscribe(d: Disposable?) {
            }

            override fun onSuccess(t: ResponseBody?) {
                try{
                    Log.d("MYTAG", "downloaded !!!!!!!")
                }
                catch (e : Exception){
                    Log.d("MYTAG", "download error EEEEEE")
                }
            }

            override fun onError(e: Throwable?) {
            }
        }

        request.subscribe(observer)

        return request
    }

    fun getAllBookInfoSearchRx(nextId: Int) : Observable<List<BookInfoFromSearch>>{
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.get_all_bookinfoRx()

        var newId = nextId - 1

        return request
                .map {
                    val result = it.map {
                        newId++
                        it.toBookInfoFromSearch(newId)
                    }
                    return@map result
                }
    }
    
    fun getAllBookInfo() : LiveData<List<BookInfo>>{
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.get_all_bookinfo()
        val result: MutableLiveData<List<BookInfo>> = MutableLiveData()
        Log.d("MYTAG", "started req")
        try {
            request.enqueue(object : Callback<List<BookInfoPojo>> {
                override fun onResponse(
                        call: Call<List<BookInfoPojo>>,
                        response: Response<List<BookInfoPojo>>
                ) {
                    if(response.code() <= 400){
                        val bookInfos = response.body()!!
                                .map {
                                    return@map it.toBookInfo()
                                }

                        result.postValue(bookInfos)

                        Log.d("MYTAG", "done")
                    }
                    else{
                    }
                }

                override fun onFailure(call: Call<List<BookInfoPojo>>, t: Throwable) {
                    Log.d("MYTAG", "failed")
//                    throw t
                }
            })
        }
        catch (e: Exception){
            Log.d("MYTAG", "exc")
        }

        return result
    }

    fun logoutRx() : Single<Unit>{
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.logoutRx()

        return request
    }

    fun loginRx(login: String, passwd: String) : Single<User>{
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.loginRx(login, passwd)

        Log.d("MYTAG", "--------------loginRx ----------------")

        return request
            .map {
                return@map it.toUser()
            }
    }

    fun checkLoginRx() : Single<User> {
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.checkLoginRx()

        val observer = object : SingleObserver<User>{
            override fun onSubscribe(d: Disposable?) {
            }

            override fun onSuccess(t: User?) {
                Log.d("MYTAG", "checkRx Success")
            }

            override fun onError(e: Throwable?) {
                Log.d("MYTAG", "checkRx Error")
            }
        }

        return request
            .map {
                return@map it.toUser()
            }
    }

    fun checkLogin() : LiveData<User>{
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.checkLogin()
        val result: MutableLiveData<User> = MutableLiveData()
        try {
            request.enqueue(object : Callback<UserPojo> {
                override fun onResponse(
                        call: Call<UserPojo>,
                        response: Response<UserPojo>
                ) {
                    if (response.isSuccessful) {
                        Log.d("MYTAG", "done")
                        cookieString = response.headers().get("Set-Cookie").toString()
                        val userPojo = response.body()
                        userPojo?.let {
                            val registrationDate = Calendar.getInstance()
                                    .apply {
                                        time = userPojo.registrationDate
                                    }
                            val lastVisitDate = Calendar.getInstance()
                                    .apply {
                                        time = userPojo.lastVisitDate
                                    }
                            val user = User(
                                    userPojo.id,
                                    userPojo.name,
                                    UserCategory.values().first { c -> c.ordinal.toString() == userPojo.userCategory },
                                    userPojo.barcode,
                                    userPojo.rfid,
                                    userPojo.canBorrowBooks,
                                    registrationDate,
                                    lastVisitDate,
                                    userPojo.imagePath,
                            )

                            result.postValue(user)
                        }

                        val u = 0
                    } else {
                        Log.d("MYTAG", "failed ${response.code()}")
                        result.postValue(null)
                    }
                }

                override fun onFailure(call: Call<UserPojo>, t: Throwable) {
                    Log.d("MYTAG", "failed")
                    result.postValue(null)
//                    throw t
                }

            })
        }
        catch (e: Exception){
            Log.d("MYTAG", "exc")
            result.postValue(null)
        }

        return result
    }

    fun login(loginUserData: LoginUserData) : LiveData<User>{
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.login(loginUserData.login, loginUserData.password)
        val result: MutableLiveData<User> = MutableLiveData()
        try {
            request.enqueue(object : Callback<UserPojo> {
                override fun onResponse(
                    call: Call<UserPojo>,
                    response: Response<UserPojo>
                ) {
                    if (response.isSuccessful) {
                        Log.d("MYTAG", "done")
                        cookieString = response.headers().get("Set-Cookie").toString()
                        val userPojo = response.body()
                        userPojo?.let {
                            val registrationDate = Calendar.getInstance()
                                .apply {
                                    time = userPojo.registrationDate
                                }
                            val lastVisitDate = Calendar.getInstance()
                                .apply {
                                    time = userPojo.lastVisitDate
                                }
                            val user = User(
                                userPojo.id,
                                userPojo.name,
                                UserCategory.values().first { c -> c.ordinal.toString() == userPojo.userCategory },
                                userPojo.barcode,
                                userPojo.rfid,
                                userPojo.canBorrowBooks,
                                registrationDate,
                                lastVisitDate,
                                userPojo.imagePath,
                            )

                            result.postValue(user)
                        }

                        val u = 0
                    } else {
                        Log.d("MYTAG", "failed ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<UserPojo>, t: Throwable) {
                    Log.d("MYTAG", "failed")
//                    throw t
                }

            })
        }
        catch (e: Exception){
            Log.d("MYTAG", "exc")
        }

        return result
    }

    fun borrow_try(bookId: Int, userId: Int){
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.borrow_try(bookId, userId)
        Log.d("MYTAG", "started req")
        try {
            request.enqueue(object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    Log.d("MYTAG", "done")
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d("MYTAG", "failed")
//                    throw t
                }

            })
        }
        catch (e: Exception){
            Log.d("MYTAG", "exc")
        }
    }

    fun completeBorrowBook(bookId: Int, userReaderId: Int, userLibrarianId: Int) : Call<Unit>{
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.completeBorrow(bookId, userReaderId, userLibrarianId)
        Log.d("MYTAG", "started req")
        try {
            request.enqueue(object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    Log.d("MYTAG", "done")
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d("MYTAG", "failed")
//                    throw t
                }

            })
        }
        catch (e: Exception){
            Log.d("MYTAG", "exc")
        }

        return request
    }

    fun completeBorrowBookRx(bookId: Int, userReaderId: Int, userLibrarianId: Int) : Completable {
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.completeBorrowRx(bookId, userReaderId, userLibrarianId)

        val observer = object : SingleObserver<User>{
            override fun onSubscribe(d: Disposable?) {
            }

            override fun onSuccess(t: User?) {
                Log.d("MYTAG", "checkRx Success")
            }

            override fun onError(e: Throwable?) {
                Log.d("MYTAG", "checkRx Error")
            }
        }

        return request
    }

    fun returnBook(bookId: Int, userReaderId: Int, userLibrarianId: Int){
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.returnBook(bookId, userReaderId, userLibrarianId)
        Log.d("MYTAG", "started req")
        try {
            request.enqueue(object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    Log.d("MYTAG", "done")
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Log.d("MYTAG", "failed")
//                    throw t
                }

            })
        }
        catch (e: Exception){
            Log.d("MYTAG", "exc")
        }
    }

    fun returnBookRx(bookId: Int, userReaderId: Int, userLibrarianId: Int) : Completable {
        val api = retrofit.create(WebAppApi::class.java)
        val request = api.returnBookRx(bookId, userReaderId, userLibrarianId)

        return request
    }

    private lateinit var cookieJar: CookieJar

    private fun gRetrofit() {
        val requestInterceptor = object : Interceptor{
            override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                val originalResponse = chain.proceed(chain.request())
                val request = chain.request()
                if(request.headers(COOKIE_HEADER).isEmpty() && logined){
                    request.headers(COOKIE_HEADER).add(cookieString)
                    return chain.proceed(request)
                }
                return originalResponse
            }
        }

        cookieJar = object : CookieJar{
//            val first_cookie = Cookie.parse(HttpUrl.parse(api_url),
//                    fromJson(sharedPreferencesRepository.getSharedPreference(activity, SharedPreferencesRepository.UserIdentityCookiesKey)))
//            val first_cookie = fromJson(sharedPreferencesRepository.getSharedPreference(activity, SharedPreferencesRepository.UserIdentityCookiesKey))
            var cookieStore: MutableMap<String, MutableList<Cookie>> = mutableMapOf()

            private fun fromJson(json: String): DataItems? = Gson().fromJson(json, DataItems::class.java)

            init {
                val gson = Gson()
                var first_cookie: MutableList<Cookie> = mutableListOf()
                try{
                    first_cookie = cookieRepository.cookies
                }
                catch(e: java.lang.Exception){
                }

                first_cookie.let {
                    val host = HttpUrl.parse(api_url)!!.host()
                    cookieStore[host] = it
                }
            }

            override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
                cookieStore.put(url.host(), cookies)
                if(api_url.contains(url.host()) && cookies.size != 0){
                    cookieRepository.cookies = cookies
                }
            }

            override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
                val result = cookieStore.get(url.host())
                return result ?: mutableListOf()
            }
        }

        val httpClient = OkHttpClient.Builder()
            .cookieJar(cookieJar)
                .addInterceptor(requestInterceptor)
//                .callTimeout(20, TimeUnit.MINUTES)
//                .connectTimeout(20, TimeUnit.MINUTES)
//                .readTimeout(30, TimeUnit.MINUTES)
//                .writeTimeout(30, TimeUnit.MINUTES)

        val gsonBuilder = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
            .create()

        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(api_url)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gsonBuilder))
                .client(httpClient.build())
                .build()

        this.retrofit = retrofit
    }

    companion object{
        private var singleton: BookNetWebAppService? = null
        fun init(){
            if(singleton == null){
                singleton = BookNetWebAppService()
            }
        }

        fun get(): BookNetWebAppService {
            if(singleton != null){
                return singleton!!
            }
            else{
                throw ExceptionInInitializerError()
            }
        }
    }
}