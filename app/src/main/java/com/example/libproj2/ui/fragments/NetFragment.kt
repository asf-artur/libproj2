package com.example.libproj2.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.libproj2.MainApp
import com.example.libproj2.R
import com.example.libproj2.api.RslApi
import com.example.libproj2.models.User
import com.example.libproj2.repositories.UserRepository
import com.example.libproj2.retrofit.contracts.BookPojo
import com.example.libproj2.retrofit.contracts.LoginUserData
import com.example.libproj2.services.BookNetWebAppService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class NetFragment : Fragment(R.layout.fragment_net) {
    lateinit var textview: TextView
    lateinit var okButton: Button
    var user: User? = null
    @Inject lateinit var userRepository: UserRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainApp.component.inject(this)

        textview = view.findViewById<TextView>(R.id.textview)
        okButton = view.findViewById(R.id.ok_button)
        okButton.isEnabled = false

        val complete_button = view.findViewById<Button>(R.id.complete_button)
        complete_button.isEnabled = false
        val return_button = view.findViewById<Button>(R.id.return_button)
        return_button.isEnabled = false

//        val d = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse("2021-05-21T00:00:00+05:00")

        val bookNetWebAppService = BookNetWebAppService.get()
//        val userLiveData = bookNetWebAppService.login(LoginUserData("b", "b"))
//        val userLiveData = bookNetWebAppService.checkLogin()
        val userLiveData = userRepository.userLiveData

        userLiveData.observe(
            viewLifecycleOwner,
            {
                user = it
                okButton.isEnabled = true
                complete_button.isEnabled = true
                return_button.isEnabled = true
            }
        )

        okButton.setOnClickListener {
//            bookNetWebAppService.borrow_try(0, user!!.id)
            bookNetWebAppService.borrow_try(3, user!!.id)
        }

        complete_button.setOnClickListener {
            bookNetWebAppService.completeBorrowBook(3, user!!.id, user!!.id)
        }

        return_button.setOnClickListener {
            bookNetWebAppService.returnBook(0, user!!.id, user!!.id)
        }

    }

    fun retrofit(){
        val httpClient = OkHttpClient.Builder()
            .callTimeout(2, TimeUnit.MINUTES)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.31.21:44331/")
            .addConverterFactory(GsonConverterFactory.create())
//            .client(httpClient.build())
            .build()

        val api = retrofit.create(RslApi::class.java)

        val req = api.searchRsl("мур")
        try {
            req.enqueue(object : Callback<List<BookPojo>> {
                override fun onResponse(
                    call: Call<List<BookPojo>>,
                    response: Response<List<BookPojo>>
                ) {
                    var t = ""
                    var i = 0
                    response.body()?.forEach {
                        t += "$i${it.author}${it.title}\n\n"
                        i++
                    }
                    textview.text = t
                }

                override fun onFailure(call: Call<List<BookPojo>>, t: Throwable) {
                    textview.text = "failed"
                    throw t
                }

            })
        }
        catch (e: Exception){

        }


//        req.enqueue(object: Callback<String>{
//            override fun onResponse(call: Call<String>, response: Response<String>) {
//                textview.text = response.body()
//            }
//
//            override fun onFailure(call: Call<String>, t: Throwable) {
//                textview.text = "failed"
//            }
//        })
    }
}