package com.example.libproj2.repositories

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.libproj2.models.BookNotification
import com.example.libproj2.models.InAppNotification
import com.example.libproj2.models.InAppNotificationType
import com.example.libproj2.models.User
import com.example.libproj2.services.BookNetWebAppService
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

class InAppNotificationsRepository(var userRepository: UserRepository) {
    private val _inAppNotifications: MutableList<InAppNotification> = mutableListOf()
    private val _inAppNotificationsVisible: MutableList<InAppNotification> = mutableListOf()
    private val bookNetWebAppService = BookNetWebAppService.get()

    val inAppNotificationsLiveDataVisible: MutableLiveData<List<InAppNotification>> = MutableLiveData()

    val inAppNotifications: List<InAppNotification>
    get() { return _inAppNotifications }

    var currentItemForChangeType: InAppNotificationType? = null
    var currentItemForChange: InAppNotification? = null
    var view: View? = null

    val snackbarCallback = object: Snackbar.Callback(){
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            super.onDismissed(transientBottomBar, event)
            if(currentItemForChange != null && currentItemForChangeType != null){
                Log.d("MYTAG", "complete change type")
                completeChangeType()
            }
        }
    }

    val snackBarOnClickListener = object : View.OnClickListener{
        override fun onClick(v: View?) {
            Log.d("MYTAG", "cancel change type")
            cancelChangeType()
        }
    }

    fun add(inAppNotification: InAppNotification){
        _inAppNotifications.add(0, inAppNotification)
        inAppNotificationsLiveDataVisible.postValue(_inAppNotifications)
    }

    fun changeType(inAppNotification: InAppNotification, inAppNotificationType: InAppNotificationType){
        val index = _inAppNotifications.firstOrNull { c -> c == inAppNotification }
        index?.let {
            currentItemForChangeType = inAppNotification.inAppNotificationType
            currentItemForChange = inAppNotification
            it.inAppNotificationType = inAppNotificationType
            inAppNotificationsLiveDataVisible.postValue(_inAppNotifications)
        }
    }

    private fun completeChangeType(){
        // Запись в базу
        val bookNotification = currentItemForChange as BookNotification
        currentItemForChange!!.isRead = true
        inAppNotificationsLiveDataVisible.postValue(_inAppNotifications)
        val observer = object : CompletableObserver {
            override fun onSubscribe(d: Disposable?) {
//                TODO("Not yet implemented")
            }

            override fun onComplete() {
//                TODO("Not yet implemented")
                Snackbar.make(view!!, "Подтверждено библиотекарем", Snackbar.LENGTH_SHORT)
                        .show()
            }

            override fun onError(e: Throwable?) {
//                TODO("Not yet implemented")
            }
        }
        val req = bookNetWebAppService.completeBorrowBookRx(bookNotification.bookCopyId, bookNotification.userId, userRepository.userLiveData.value!!.id)
        req.subscribe(observer)
    }

    private fun cancelChangeType(){
        currentItemForChangeType?.let {
            currentItemForChange?.inAppNotificationType = it
            inAppNotificationsLiveDataVisible.postValue(_inAppNotifications)

            currentItemForChangeType = null
            currentItemForChange = null
        }
    }

    fun remove(inAppNotification: InAppNotification){
        _inAppNotifications.remove(inAppNotification)
    }
}