package com.example.libproj2.ui.fragments.books_history

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libproj2.R
import com.example.libproj2.models.BookInfo
import com.example.libproj2.repositories.BooksHistoryRepository
import com.example.libproj2.services.ImageLoadService
import com.example.libproj2.ui.fragments.BaseMainActivityFragment
import com.example.libproj2.ui.fragments.BookDetailsHistoryFragment

class BooksHistoryFragment : BaseMainActivityFragment(R.layout.fragment_books_history) {
    val booksHistoryRepository = BooksHistoryRepository.get()
    val imageLoadService = ImageLoadService()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val books_history_textview = view.findViewById<TextView>(R.id.books_history_textview)
        books_history_textview.text = getString(R.string.books_history, booksHistoryRepository.books.size.toString())

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = BooksHistoryAdapter(booksHistoryRepository.books, ::loadImage, ::onItemClick)
    }

    fun loadImage(path: String): Drawable?{
        return imageLoadService.loadImage(path, requireContext())
    }

    fun onItemClick(bookInfo: BookInfo){
        val bundle = bundleOf("BOOKID" to bookInfo.id)
        findNavController().navigate(R.id.action_booksHistoryFragment_to_bookDetailsHistoryFragment, bundle)
    }
}