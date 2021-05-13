package com.example.libproj2.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.libproj2.R
import com.example.libproj2.models.BookCopy
import com.example.libproj2.repositories.AllBookCopiesRepository
import com.example.libproj2.repositories.BookCopiesRepository
import com.example.libproj2.services.ImageLoadService
import com.example.libproj2.ui.fragments.all_books.AllBooksFragment

class TryBorrowBookWaitingFragment : BaseMainActivityFragment(R.layout.fragment_try_borrow_book_waiting) {
    val bookCopiesRepository = BookCopiesRepository.get()
    val imageLoadService = ImageLoadService()
    val allBookCopiesRepository = AllBookCopiesRepository.get()

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookId = requireArguments().getInt(BOOK_ID)
        val b = findNavController().backQueue
        val t = b.any {
            c -> c.destination.label == AllBooksFragment::class.simpleName
        }

        val bookCopyNullable: BookCopy
        if(t){
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
            drawable?.let {
                imageView.setImageDrawable(drawable)
            }
        }

        val author_textview = view.findViewById<TextView>(R.id.author_textview)
        author_textview.text = bookCopy.author

        val title_textview = view.findViewById<TextView>(R.id.title_textview)
        title_textview.text = bookCopy.title

        val barcode_number_textView = view.findViewById<TextView>(R.id.barcode_number_textView)
        barcode_number_textView.text = bookCopy.barcode

        val cancel_button = view.findViewById<Button>(R.id.cancel_button)
        cancel_button.setOnClickListener {
        }
    }

    companion object{
        val BOOK_ID = "BOOK_ID"
    }
}