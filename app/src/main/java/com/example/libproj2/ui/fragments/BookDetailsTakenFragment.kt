package com.example.libproj2.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.os.HandlerExecutor
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.libproj2.GlobalViewModel
import com.example.libproj2.MainApp
import com.example.libproj2.R
import com.example.libproj2.models.BookCopy
import com.example.libproj2.models.UserCategory
import com.example.libproj2.models.toText
import com.example.libproj2.repositories.*
import com.example.libproj2.services.BookNetWebAppService
import com.example.libproj2.services.BookService
import com.example.libproj2.services.ImageLoadService
import com.example.libproj2.ui.fragments.all_books.AllBooksFragment
import com.example.libproj2.ui.fragments.books_on_hand.BooksOnHandFragment
import com.example.libproj2.ui.fragments.catalog.CatalogFragment
import com.example.libproj2.utils.ToolbarOptions
import com.example.libproj2.utils.toDateString
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Executors
import javax.inject.Inject

private const val BOOKID = "BOOKID"
class BookDetailsTakenFragment : BaseMainActivityFragment(R.layout.fragment_book_details_taken) {
    val globalViewModel: GlobalViewModel by activityViewModels()
    val bookNetWebAppService = BookNetWebAppService.get()
    val booksOnHandRepository = BooksOnHandRepository.get()
    val readerBookOnHandsRepository = ReaderBookOnHandsRepository.get()
    val bookSearchResultRepository = BookSearchResultRepository.get()
    val bookCopiesRepository = BookCopiesRepository.get()
    val imageLoadService = ImageLoadService()
    private lateinit var bookCopy: BookCopy
    val bookService = BookService()
    val allBookCopiesRepository = AllBookCopiesRepository.get()
    @Inject lateinit var userRepository: UserRepository

    override var toolbarTitle: String? = "Подробнее о книге"

    override val toolbarOptions: ToolbarOptions = ToolbarOptions(R.menu.top_menu_books_details_taken_fragment){
        menuItem ->
        when(menuItem.itemId){
            R.id.barcode_show ->{
                val bundle = bundleOf("BOOKID" to bookCopy.id)
                findNavController().navigate(R.id.action_bookDetailsTakenFragment_to_showBarcodeFragment, bundle)
                true
            }
            else -> false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainApp.component.inject(this)

        val bookId = requireArguments().getInt(BOOKID)
        val b = findNavController().previousBackStackEntry

        val bookCopyNullable: BookCopy
        if(b!!.destination.label == AllBooksFragment::class.simpleName){
            bookCopyNullable = allBookCopiesRepository.books.first{c -> c.id == bookId}
        }
        else if(b.destination.label == ReaderFoundFragment::class.simpleName){
            bookCopyNullable = readerBookOnHandsRepository.books.first{c -> c.id == bookId}
        }
        else if(b.destination.label == "fragment_catalog"){
            bookCopyNullable = bookCopiesRepository.books.first{c -> c.id == bookId}
        }
        else{
            bookCopyNullable = booksOnHandRepository.books.first{c -> c.id == bookId}
        }
        bookCopy = bookCopyNullable

        val imageView = view.findViewById<ImageView>(R.id.imageView)
        imageView.setImageResource(R.drawable.book_no_image)
        bookCopy.bookInfo.imagePath?.let {
            val drawable = imageLoadService.loadImage(it, requireContext())
            drawable?.let{
                imageView.setImageDrawable(drawable)
            }
        }

        val author_textview = view.findViewById<TextView>(R.id.author_textview)
        author_textview.text = bookCopy.author

        val title_textview = view.findViewById<TextView>(R.id.title_textview)
        title_textview.text = bookCopy.title

        val return_date_textview = view.findViewById<TextView>(R.id.return_date_textview)
        return_date_textview.text = "Вернуть до ${bookCopy.returnDate?.toDateString()}"

        val other_textview = view.findViewById<TextView>(R.id.other_textview)
        other_textview.text = bookCopy.bookInfo.toText()

        val barcode_number_textView = view.findViewById<TextView>(R.id.barcode_number_textView)
        barcode_number_textView.text = bookCopy.barcode

        val user = userRepository.userLiveData.value!!
        val book_return_button = view.findViewById<Button>(R.id.book_return_button)
        if(user.userCategory == UserCategory.Admin || user.userCategory == UserCategory.Librarian){
            book_return_button.visibility = View.VISIBLE
        }
        val nav = findNavController()
        val h = Handler(requireContext().mainLooper){
//            allBookCopiesRepository.load()
            booksOnHandRepository.load()
            bookCopiesRepository.tryRefresh(bookCopy, bookNetWebAppService)
            nav.navigateUp()
            true
        }
        book_return_button.setOnClickListener {
            val observer = object : CompletableObserver{
                override fun onSubscribe(d: Disposable?) {
//                    TODO("Not yet implemented")
                }

                override fun onComplete() {
//                    TODO("Not yet implemented")
                    h.sendMessage(Message())
                }

                override fun onError(e: Throwable?) {
//                    TODO("Not yet implemented")
                }

            }

            val comp = bookService.returnBook(bookCopy, userRepository.userLiveData.value!!.id)
            comp.subscribe(observer)
        }
    }

    companion object{
        fun init(bookId: Int): BookDetailsTakenFragment {
            return BookDetailsTakenFragment().apply {
                arguments = bundleOf(BOOKID to bookId)
            }
        }
    }
}