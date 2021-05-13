package com.example.libproj2.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.example.libproj2.GlobalViewModel
import com.example.libproj2.R
import com.example.libproj2.models.toText
import com.example.libproj2.repositories.BooksHistoryRepository
import com.example.libproj2.services.ImageLoadService

private const val BOOKID = "BOOKID"
class BookDetailsHistoryFragment : BaseMainActivityFragment(R.layout.fragment_book_details_history) {
    val globalViewModel: GlobalViewModel by activityViewModels()
    val booksHistoryRepository = BooksHistoryRepository.get()
    val imageLoadService = ImageLoadService()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookId = requireArguments().getInt(BOOKID)
        val bookInfo = booksHistoryRepository.books.first{ c -> c.id == bookId}

        val imageView = view.findViewById<ImageView>(R.id.imageView)
        imageView.setImageResource(R.drawable.book_no_image)
        bookInfo.imagePath?.let {
            val drawable = imageLoadService.loadImage(it, requireContext())
            drawable?.let{
                imageView.setImageDrawable(drawable)
            }
        }

        val author_textview = view.findViewById<TextView>(R.id.author_textview)
        author_textview.text = bookInfo.authors?.get(0)

        val title_textview = view.findViewById<TextView>(R.id.title_textview)
        title_textview.text = bookInfo.title

        val other_textview = view.findViewById<TextView>(R.id.other_textview)
        other_textview.text = bookInfo.toText()
    }

    companion object{
        fun init(bookId: Int): BookDetailsHistoryFragment {
            return BookDetailsHistoryFragment().apply {
                arguments = bundleOf(BOOKID to bookId)
            }
        }
    }
}