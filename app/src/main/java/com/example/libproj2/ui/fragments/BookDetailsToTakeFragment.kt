package com.example.libproj2.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.libproj2.GlobalViewModel
import com.example.libproj2.MainApp
import com.example.libproj2.R
import com.example.libproj2.models.BookCopy
import com.example.libproj2.models.UserCategory
import com.example.libproj2.repositories.AllBookCopiesRepository
import com.example.libproj2.repositories.BookCopiesRepository
import com.example.libproj2.repositories.UserRepository
import com.example.libproj2.services.BookService
import com.example.libproj2.services.ImageLoadService
import com.example.libproj2.ui.fragments.all_books.AllBooksFragment
import javax.inject.Inject

private const val BOOKID = "BOOKID"
class BookDetailsToTakeFragment : BaseMainActivityFragment(R.layout.fragment_book_details_to_take){
    val bookCopiesRepository = BookCopiesRepository.get()
    val imageLoadService = ImageLoadService()
    val bookService = BookService()
    @Inject lateinit var userRepository: UserRepository
    val allBookCopiesRepository = AllBookCopiesRepository.get()
    val globalViewModel : GlobalViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainApp.component.inject(this)

        val bookId = requireArguments().getInt(BOOKID)
        val b = findNavController().previousBackStackEntry

        val bookCopyNullable: BookCopy
        if(b!!.destination.label == AllBooksFragment::class.simpleName){
            bookCopyNullable = allBookCopiesRepository.books.first{c -> c.id == bookId}
        }
        else{
            bookCopyNullable = bookCopiesRepository.books.first{c -> c.id == bookId}
        }
        val bookCopy = bookCopyNullable

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

        val barcode_number_textView = view.findViewById<TextView>(R.id.barcode_number_textView)
        barcode_number_textView.text = bookCopy.barcode

        val user = userRepository.userLiveData.value!!
        val book_add_button = view.findViewById<Button>(R.id.book_add_button)
        book_add_button.setOnClickListener {
            if(user.userCategory == UserCategory.Librarian || user.userCategory == UserCategory.Admin){
                val mainLinearLayout = view.findViewById<LinearLayout>(R.id.mainLinearLayout)
                if(globalViewModel.selectedReaderUser != null){
                    bookService.borrowBookLibrarian(bookCopy, globalViewModel.selectedReaderUser!!, user, mainLinearLayout)
                }
                else{
                    bookService.borrowBookLibrarian(bookCopy, user, user, mainLinearLayout)
                }
                findNavController().navigateUp()
            }
            else{
                bookService.tryBorrowBook(bookCopy, user)
                val bundle = bundleOf(TryBorrowBookWaitingFragment.BOOK_ID to bookCopy.id)
                findNavController().navigate(R.id.action_bookDetailsToTakeFragment_to_tryBorrowBookWaitingFragment, bundle)
            }
        }
    }

    companion object{
        fun init(bookId: Int): BookDetailsToTakeFragment {
            return BookDetailsToTakeFragment().apply {
                arguments = bundleOf(BOOKID to bookId)
            }
        }
    }
}