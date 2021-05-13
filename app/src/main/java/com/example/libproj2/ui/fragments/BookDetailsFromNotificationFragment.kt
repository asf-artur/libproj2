package com.example.libproj2.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.example.libproj2.GlobalViewModel
import com.example.libproj2.R
import com.example.libproj2.repositories.AllBookCopiesRepository
import com.example.libproj2.repositories.BookCopiesRepository
import com.example.libproj2.services.ImageLoadService

private const val BOOKID = "BOOKID"
class BookDetailsFromNotificationFragment : BaseMainActivityFragment(R.layout.fragment_book_details_history) {
    val globalViewModel: GlobalViewModel by activityViewModels()
    val bookCopiesRepository = BookCopiesRepository.get()
    val allBookCopiesRepository = AllBookCopiesRepository.get()
    val imageLoadService = ImageLoadService()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookId = requireArguments().getInt(BOOKID)
        val bookCopy = allBookCopiesRepository.books.first{ c -> c.id == bookId}

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
    }

    companion object{
        fun init(bookId: Int): BookDetailsFromNotificationFragment {
            return BookDetailsFromNotificationFragment().apply {
                arguments = bundleOf(BOOKID to bookId)
            }
        }
    }
}