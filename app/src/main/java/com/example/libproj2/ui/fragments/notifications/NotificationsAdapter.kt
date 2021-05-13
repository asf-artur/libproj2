package com.example.libproj2.ui.fragments.notifications

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.libproj2.MainApp
import com.example.libproj2.R
import com.example.libproj2.models.*
import com.example.libproj2.repositories.AllBookCopiesRepository
import com.example.libproj2.repositories.BookCopiesRepository
import com.example.libproj2.repositories.UserFromNotificationRepository
import com.example.libproj2.repositories.UserRepository
import com.example.libproj2.services.BookNetWebAppService
import com.example.libproj2.utils.makeLinks
import com.example.libproj2.utils.toDateTimeString
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.functions.Consumer
import java.lang.Exception
import javax.inject.Inject

class NotificationsAdapter(
    val inAppNotifications: List<InAppNotification>,
    val context: Context,
    val onUserClick: (user: User?) -> Unit,
    val onBookClick: (book: BookCopy?) -> Unit,
    val onOkClick: (inAppNotification: InAppNotification) -> Unit,
    val onRejectClick: (inAppNotification: InAppNotification) -> Unit,
):
    RecyclerView.Adapter<NotificationsAdapter.NotificationsHolder>(){

    init {
        MainApp.component.inject(this)
    }

    val bookCopiesRepository = BookCopiesRepository.get()
    val allBookCopiesRepository = AllBookCopiesRepository.get()
    val bookNetWebAppService = BookNetWebAppService.get()
    @Inject lateinit var userRepository: UserRepository
    @Inject lateinit var userFromNotificationRepository: UserFromNotificationRepository

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationsHolder {
        val inflater = LayoutInflater.from(parent.context)

        val layoutId = when(viewType){
            getEnumNumber(InAppNotificationType.TryBorrowBook) -> R.layout.item_notification_lib_tryborrowbook
            getEnumNumber(InAppNotificationType.TryBorrowBookRejected) -> R.layout.item_notification_lib_tryborrowbook_rejected
            getEnumNumber(InAppNotificationType.BookIsBorrowed) -> R.layout.item_notification_lib_tryborrowbook_success
            getEnumNumber(InAppNotificationType.Important) -> R.layout.item_notification_lib_news
            else -> throw Exception()
        }

        val view = inflater.inflate(layoutId, parent, false)
        return NotificationsHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationsHolder, position: Int) {
        val inAppNotification = inAppNotifications[position]
        when(inAppNotification.inAppNotificationType){
            InAppNotificationType.TryBorrowBook -> holder.bind_TryBorrowBook(inAppNotification)
            InAppNotificationType.TryBorrowBookRejected -> holder.bind_TryBorrowBookRejected(inAppNotification)
            InAppNotificationType.BookIsBorrowed -> holder.bind_BookIsBorrowed(inAppNotification)
            InAppNotificationType.Important -> holder.bind_News(inAppNotification)
            else -> throw Exception()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getEnumNumber(inAppNotifications[position].inAppNotificationType)
    }

    private fun getEnumNumber(appNotificationType: InAppNotificationType): Int{
        return InAppNotificationType.values()
            .first { c -> c == appNotificationType}
            .ordinal
    }

    override fun getItemCount(): Int = inAppNotifications.size

    inner class NotificationsHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind_TryBorrowBook(inAppNotification: InAppNotification){
            val bookNotification = inAppNotification as BookNotification

            val date_textView = itemView.findViewById<TextView>(R.id.date_textView)
            val content_textView = itemView.findViewById<TextView>(R.id.content_textView)
            val ok_button = itemView.findViewById<Button>(R.id.ok_button)
            val reject_button = itemView.findViewById<Button>(R.id.reject_button)

            date_textView.text = inAppNotification.date.toDateTimeString()

            var user = userFromNotificationRepository.users.firstOrNull { c -> c.id == bookNotification.userId}
            var bookCopy = allBookCopiesRepository.books.firstOrNull { c -> c.id == bookNotification.bookCopyId }
            if(user != null && bookCopy != null){
                val bookTitle = bookCopy.title ?: "notitle"
                val pair1 = Pair(user.name, View.OnClickListener {
                    onUserClick(user)
                })
                val pair2 = Pair(bookTitle, View.OnClickListener {
                    onBookClick(bookCopy)
                })

                val text = context.getString(R.string.notification_tryborrowbook_lib, user.name, bookTitle)
                content_textView.text = text
                content_textView.makeLinks(pair1, pair2)
            }
            else{
                userFromNotificationRepository.addUser(bookNotification.userId)
                        .zipWith(bookNetWebAppService.getBookCopyById(bookNotification.bookCopyId), BiFunction { t1, t2 ->
                            bookCopy = t2;
                            allBookCopiesRepository.add(t2)
                            t1
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(Consumer {
                            userFromNotificationRepository.users.add(it)
                            user = it
                            val bookTitle = bookCopy?.title ?: "notitle"

                            val pair1 = Pair(user!!.name, View.OnClickListener {
//                Snackbar.make(itemView, userName, Snackbar.LENGTH_SHORT).show()
                                onUserClick(user)
                            })
                            val pair2 = Pair(bookTitle, View.OnClickListener {
//                Snackbar.make(itemView, bookTitle, Snackbar.LENGTH_SHORT).show()
                                onBookClick(bookCopy)
                            })

                            val text = context.getString(R.string.notification_tryborrowbook_lib, user!!.name, bookTitle)
                            content_textView.text = text
                            content_textView.makeLinks(pair1, pair2)
                            notifyItemChanged(inAppNotifications.indexOf(inAppNotification))
                        },
                                {
                                    val q = 0
                                    Log.d("MYTAG", it!!.message.toString())
                                })
            }

            ok_button.setOnClickListener {
                onOkClick(inAppNotification)
                notifyItemChanged(inAppNotifications.indexOf(inAppNotification))
            }

            reject_button.setOnClickListener {
                onRejectClick(inAppNotification)
                notifyItemChanged(inAppNotifications.indexOf(inAppNotification))
            }
        }

        fun bind_TryBorrowBookRejected(inAppNotification: InAppNotification){
            val bookNotification = inAppNotification as BookNotification

            val date_textView = itemView.findViewById<TextView>(R.id.date_textView)
            val content_textView = itemView.findViewById<TextView>(R.id.content_textView)

            date_textView.text = inAppNotification.date.toDateTimeString()

            val user = userRepository.user
            val bookCopy = allBookCopiesRepository.books.firstOrNull { c -> c.id == bookNotification.bookCopyId }
            val bookTitle = bookCopy?.title ?: "notitle"

            val pair1 = Pair(user.name, View.OnClickListener {
//                Snackbar.make(itemView, userName, Snackbar.LENGTH_SHORT).show()
                onUserClick(userRepository.user)
            })
            val pair2 = Pair(bookTitle, View.OnClickListener {
                Snackbar.make(itemView, bookTitle, Snackbar.LENGTH_SHORT).show()
                onBookClick(bookCopy)
            })

            val text = context.getString(R.string.notification_rejectborrowbook_lib, user.name, bookTitle)
            content_textView.text = text
            content_textView.makeLinks(pair1, pair2)
        }

        fun bind_BookIsBorrowed(inAppNotification: InAppNotification){
            val bookNotification = inAppNotification as BookNotification

            val date_textView = itemView.findViewById<TextView>(R.id.date_textView)
            val content_textView = itemView.findViewById<TextView>(R.id.content_textView)

            date_textView.text = inAppNotification.date.toDateTimeString()

            val user = userRepository.user
            val bookCopy = allBookCopiesRepository.books.firstOrNull { c -> c.id == bookNotification.bookCopyId }
            val bookTitle = bookCopy?.title ?: "notitle"

            val pair1 = Pair(user.name, View.OnClickListener {
//                Snackbar.make(itemView, userName, Snackbar.LENGTH_SHORT).show()
                onUserClick(userRepository.user)
            })
            val pair2 = Pair(bookTitle, View.OnClickListener {
                Snackbar.make(itemView, bookTitle, Snackbar.LENGTH_SHORT).show()
                onBookClick(bookCopy)
            })

            val text = context.getString(R.string.notification_bookisborrowed_lib, user.name, bookTitle)
            content_textView.text = text
            content_textView.makeLinks(pair1, pair2)
        }

        fun bind_News(inAppNotification: InAppNotification){
            val date_textView = itemView.findViewById<TextView>(R.id.date_textView)
            val content_textView = itemView.findViewById<TextView>(R.id.content_textView)
            val title_textView = itemView.findViewById<TextView>(R.id.textView)

            date_textView.text = inAppNotification.date.toDateTimeString()
            content_textView.text = inAppNotification.content
            title_textView.text = inAppNotification.title
        }
    }
}