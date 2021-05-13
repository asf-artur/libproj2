package com.example.libproj2.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.widget.addTextChangedListener
import com.example.libproj2.MainApp
import com.example.libproj2.R
import com.example.libproj2.models.EmptyUser
import com.example.libproj2.models.User
import com.example.libproj2.repositories.UserRepository
import com.example.libproj2.retrofit.contracts.LoginUserData
import com.example.libproj2.services.BookNetWebAppService
import com.example.libproj2.services.FirebaseService
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import org.reactivestreams.Subscription
import java.util.concurrent.Flow
import javax.inject.Inject

class LoginFragment : BaseMainActivityFragment(R.layout.fragment_login) {
    @Inject lateinit var userRepository: UserRepository
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainApp.component.inject(this)

//        val userRepository = UserRepository.get()

        val login_textedit = view.findViewById<TextInputEditText>(R.id.login_textedit)
        val password_textedit = view.findViewById<TextInputEditText>(R.id.password_textedit)
        val ok_button = view.findViewById<Button>(R.id.ok_button)

        login_textedit.addTextChangedListener {
            ok_button.isEnabled = login_textedit.text.toString() != "" && password_textedit.text.toString() != ""
        }


        password_textedit.addTextChangedListener {
            ok_button.isEnabled = login_textedit.text.toString() != "" && password_textedit.text.toString() != ""
        }

        var observer = object : SingleObserver<User> {
            override fun onSubscribe(d: Disposable?) {
            }

            override fun onSuccess(t: User?) {
                t?.let{
                    userRepository.add(it)
                }
                Log.d("MYTAG", "onSuccess")
            }

            override fun onError(e: Throwable?) {
                Snackbar
                        .make(view, "Неправильно введен пароль или такого пользователя не существует", Snackbar.LENGTH_SHORT)
                        .show()
                userRepository.userLiveData.postValue(null)
                Log.d("MYTAG", "onError")
            }
        }

        val sub = object : CompletableObserver{
            override fun onSubscribe(d: Disposable?) {
//                TODO("Not yet implemented")
            }

            override fun onComplete() {
//                TODO("Not yet implemented")
            }

            override fun onError(e: Throwable?) {
//                TODO("Not yet implemented")
                Log.d("MYTAG", "error ${e?.message}")
            }

        }

//        val bookNetWebAppService = BookNetWebAppService.get()
//        bookNetWebAppService.loginRx(login_textedit.text.toString(), password_textedit.text.toString())
//                .doOnSuccess {
//                    userRepository.add(it)
//                }
//                .flatMapObservable{
//                    c -> FirebaseService().getFirebaseToken()
//                }
//                .flatMapCompletable {
//                    return@flatMapCompletable bookNetWebAppService.sendClientToken(it)
//                }
//                .subscribe(sub)

        ok_button.setOnClickListener {
            val bookNetWebAppService = BookNetWebAppService.get()
            bookNetWebAppService.loginRx(login_textedit.text.toString(), password_textedit.text.toString())
                    .doOnSuccess {
                        userRepository.add(it)
                    }
                    .flatMapObservable{
                        c -> FirebaseService().getFirebaseToken()
                    }
                    .flatMapCompletable {
                        return@flatMapCompletable bookNetWebAppService.sendClientToken(it)
                    }
                    .subscribe(sub)
        }
    }
}