package com.example.libproj2.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import com.example.libproj2.R
import com.example.libproj2.repositories.BookInfoRepository
import com.example.libproj2.repositories.BookSearchResultRepository
import com.example.libproj2.services.ImageLoadService

private const val BOOKID = "BOOKID"
class BookSearchResultFragment : BaseMainActivityFragment(R.layout.fragment_book_details_history) {
    val bookInfoRepository = BookInfoRepository.get()
    val bookSearchResultRepository = BookSearchResultRepository.get()
    val imageLoadService = ImageLoadService()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookId = requireArguments().getInt(BOOKID)
//        val bookCopy = bookInfoRepository.books.first{ c -> c.id == bookId}
        val bookCopy = bookSearchResultRepository.books.first{ c -> c.id == bookId}

        val imageView = view.findViewById<ImageView>(R.id.imageView)
        imageView.setImageResource(R.drawable.book_no_image)
        bookCopy.imagePath?.let {
            val drawable = imageLoadService.loadImage(it, requireContext())
            drawable?.let{
                imageView.setImageDrawable(drawable)
            }
        }

        val author_textview = view.findViewById<TextView>(R.id.author_textview)
        author_textview.text = bookCopy.authors?.getOrNull(0) ?: ""

        val title_textview = view.findViewById<TextView>(R.id.title_textview)
        title_textview.text = bookCopy.title
    }

    companion object{
        fun init(bookId: Int): BookSearchResultFragment {
            return BookSearchResultFragment().apply {
                arguments = bundleOf(BOOKID to bookId)
            }
        }
    }
}