package com.example.libproj2.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import com.example.libproj2.R
import com.example.libproj2.models.BookCopy
import com.example.libproj2.repositories.BooksOnHandRepository
import com.example.libproj2.services.ImageLoadService

private const val BOOKID = "BOOKID"
class ShowBarcodeFragment : BaseMainActivityFragment(R.layout.fragment_show_barcode) {
    val booksOnHandRepository = BooksOnHandRepository.get()
    val imageLoadService = ImageLoadService()
    private lateinit var bookCopy: BookCopy

    override var toolbarTitle: String? = "Штрихкод"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookId = requireArguments().getInt(BOOKID)
        bookCopy = booksOnHandRepository.books.first { c -> c.id == bookId }

        val imageView = view.findViewById<ImageView>(R.id.imageView)

        bookCopy.barcode?.let {
            imageLoadService.setBarcodeImage(it, imageView)
        }

        val title_textview = view.findViewById<TextView>(R.id.title_textview)
        title_textview.text = bookCopy.title

        val author_textview = view.findViewById<TextView>(R.id.author_textview)
        author_textview.text = bookCopy.author
    }

    companion object{
        fun init(bookId: Int): ShowBarcodeFragment {
            return ShowBarcodeFragment().apply {
                arguments = bundleOf(BOOKID to bookId)
            }
        }
    }
}