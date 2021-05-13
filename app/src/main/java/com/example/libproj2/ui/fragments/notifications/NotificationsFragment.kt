package com.example.libproj2.ui.fragments.notifications

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libproj2.GlobalViewModel
import com.example.libproj2.MainApp
import com.example.libproj2.R
import com.example.libproj2.models.*
import com.example.libproj2.repositories.BookCopiesRepository
import com.example.libproj2.repositories.InAppNotificationsRepository
import com.example.libproj2.ui.fragments.BaseMainActivityFragment
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject


class NotificationsFragment()
    : BaseMainActivityFragment(R.layout.fragment_notifications) {
    @Inject lateinit var inAppNotificationsRepository : InAppNotificationsRepository
    val globalViewModel: GlobalViewModel by activityViewModels()

    override var toolbarTitle: String? = "Уведомления"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainApp.component.inject(this)

        val inAppNotifications = inAppNotificationsRepository.inAppNotifications
        val imageView = view.findViewById<ImageView>(R.id.imageView)

        val recyclerview = view.findViewById<RecyclerView>(R.id.recyclerview)
        if(inAppNotifications.isEmpty()){

            imageView.visibility = View.VISIBLE
        }

        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        inAppNotificationsRepository.inAppNotificationsLiveDataVisible.observe(
            viewLifecycleOwner,
            {
                it?.let{
                    if(it.isNotEmpty()){
                        imageView.visibility = View.GONE
                    }
                }
                recyclerview.adapter = NotificationsAdapter(inAppNotifications, requireContext(), ::onUserClick, ::onBookClick, ::onOkClick, ::onRejectClick)
            }
        )
    }

    fun onUserClick(user: User?){
        globalViewModel.selectedReaderUser = user
        findNavController().navigate(R.id.action_notificationsFragment_to_libraryCardFragment2)
    }

    fun onBookClick(bookCopy: BookCopy?){
        bookCopy?.let {
            val bundle = bundleOf("BOOKID" to bookCopy.id)
            findNavController().navigate(R.id.action_notificationsFragment_to_bookDetailsFromNotificationFragment, bundle)
        }
    }

    fun onOkClick(inAppNotification: InAppNotification){
        val oldType = inAppNotification.inAppNotificationType
        Snackbar.make(requireView(), "", Snackbar.LENGTH_SHORT)
                .apply {
                    this.setAction("Отменить", inAppNotificationsRepository.snackBarOnClickListener)
                    this.addCallback(inAppNotificationsRepository.snackbarCallback)
                    this.show()
                }
        inAppNotificationsRepository.view = requireView()
        inAppNotificationsRepository.changeType(inAppNotification, InAppNotificationType.BookIsBorrowed)
    }

    fun onRejectClick(inAppNotification: InAppNotification){
        val oldType = inAppNotification.inAppNotificationType
        Snackbar.make(requireView(), "", Snackbar.LENGTH_SHORT)
                .apply {
                    this.setAction("Отменить", inAppNotificationsRepository.snackBarOnClickListener)
                    this.addCallback(inAppNotificationsRepository.snackbarCallback)
                    this.show()
                }
        inAppNotificationsRepository.changeType(inAppNotification, InAppNotificationType.TryBorrowBookRejected)
    }
}