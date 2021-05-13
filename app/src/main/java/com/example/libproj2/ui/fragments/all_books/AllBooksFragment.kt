package com.example.libproj2.ui.fragments.all_books

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libproj2.R
import com.example.libproj2.models.BookCopy
import com.example.libproj2.models.BookStatus
import com.example.libproj2.repositories.AllBookCopiesRepository
import com.example.libproj2.repositories.BookCopiesRepository
import com.example.libproj2.repositories.BooksOnHandRepository
import com.example.libproj2.services.ImageLoadService
import com.example.libproj2.ui.fragments.BaseMainActivityFragment
import com.example.libproj2.ui.fragments.BookDetailsToTakeFragment
import io.reactivex.rxjava3.functions.Consumer

class AllBooksFragment : BaseMainActivityFragment(R.layout.fragment_all_books) {
    val allBookCopiesRepository = AllBookCopiesRepository.get()
    val imageLoadService = ImageLoadService()
    val bookCopiesRepository = BookCopiesRepository.get()
    val booksOnHandRepository = BooksOnHandRepository.get()

    override var toolbarTitle: String? = "Посмотреть все экземпляры книг(Админ)"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        allBookCopiesRepository.load()

        val books_history_textview = view.findViewById<TextView>(R.id.books_history_textview)
        books_history_textview.text = getString(R.string.all_books, allBookCopiesRepository.books.size.toString())

        val re = allBookCopiesRepository.load()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        allBookCopiesRepository.booksLiveData.observe(viewLifecycleOwner,
                {
                    recyclerView.adapter = AllBooksAdapter(it, ::loadImage, ::onItemClick)
                })
//        recyclerView.adapter = AllBooksAdapter(allBookCopiesRepository.books, ::loadImage, ::onItemClick)
    }

    fun loadImage(path: String): Drawable?{
        return imageLoadService.loadImage(path, requireContext())
    }

    fun onItemClick(bookCopy: BookCopy){
        when(bookCopy.bookStatus){
            BookStatus.InStock -> {
//                if(!bookCopiesRepository.books.contains(bookCopy)){
//                    bookCopiesRepository.add(bookCopy)
//                }
                val bundle = bundleOf("BOOKID" to bookCopy.id)
//                val navController = Navigation.findNavController(requireActivity(), R.id.fragment_container)
                findNavController().navigate(R.id.action_allBooksFragment_to_bookDetailsToTakeFragment, bundle)
            }
            BookStatus.NotInStock ->{
                if(!bookCopiesRepository.books.contains(bookCopy)){
                    bookCopiesRepository.add(bookCopy)
                }
                val bundle = bundleOf("BOOKID" to bookCopy.id)
                findNavController().navigate(R.id.action_allBooksFragment_to_bookDetailsTakenFragment, bundle)
            }
        }
    }
}