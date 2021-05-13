package com.example.libproj2.ui.fragments.search_results

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libproj2.R
import com.example.libproj2.models.BookInfo
import com.example.libproj2.repositories.BookCopiesRepository
import com.example.libproj2.repositories.BooksOnHandRepository
import com.example.libproj2.repositories.OldSearchResultRepository
import com.example.libproj2.services.BookNetSearchService
import com.example.libproj2.services.ImageLoadService
import com.example.libproj2.ui.fragments.*
import com.example.libproj2.utils.SystemServicesStorage
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

private const val SEARCH_TERM = "SEARCH_TERM"
class SearchResultsFragment : BaseMainActivityFragment(R.layout.fragment_search_results) {
    val booksOnHandRepository = BooksOnHandRepository.get()
    val bookCopiesRepository = BookCopiesRepository.get()
    val oldSearchResultRepository = OldSearchResultRepository.get()
    val imageLoadService = ImageLoadService()
    val bookNetSearchService = BookNetSearchService()

    override var toolbarTitle: String? = "Библиотека"

    lateinit var textInput: TextInputEditText
    var searchTerm: String = ""

    override fun setupTopAppBar() {
        super.setupTopAppBar()
        topAppBar.visibility = View.GONE
        val v = view!!
        v.visibility = View.VISIBLE

        textInput = v.findViewById<TextInputEditText>(R.id.textinput)
        textInput.setText(searchTerm)
        val textInputLayout = v.findViewById<TextInputLayout>(R.id.textInputLayout)
        textInputLayout.setEndIconOnClickListener {
            textInput.text?.clear()
            textInput.clearFocus()
            val systemServicesStorage = SystemServicesStorage.get()
            systemServicesStorage.inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("MYTAG", "paused")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchTerm = requireArguments().getString(SEARCH_TERM)!!

        val books_history_textview = view.findViewById<TextView>(R.id.books_history_textview)
        books_history_textview.text = "Результаты поиска:"

        val mainLinearLayout = view.findViewById<LinearLayout>(R.id.mainLinearLayout)
        mainLinearLayout.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                val systemServicesStorage = SystemServicesStorage.get()
                systemServicesStorage.inputMethodManager.hideSoftInputFromWindow(mainLinearLayout.windowToken, 0)
            }
        }


        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

//        var result = bookNetSearchService.search(searchTerm)
        var result = bookNetSearchService.searchInGoogleBooks(searchTerm)
        result.observe(
                viewLifecycleOwner,
                {
                    it?.let {
//                        recyclerView.adapter = SearchResultsAdapter(it.toList(), ::loadImage, ::onItemClick)
                        val progressbar = view.findViewById<ProgressBar>(R.id.progressbar)
                        progressbar.visibility = View.GONE
                    }
                }
        )

//        recyclerView.adapter = CatalogAdapter(booksOnHandRepository.books, ::loadImage, ::onItemClick)
    }

    fun loadImage(path: String): Drawable?{
        return imageLoadService.loadImage(path, requireContext())
    }

    fun onItemClick(bookInfo: BookInfo){
//        val bookDetailsTakenFragment = BookDetailsTakenFragment.init(bookInfo.id)
//        goToFragment(bookDetailsTakenFragment)
        val bundle = bundleOf("BOOKID" to bookInfo.id)
        findNavController().navigate(R.id.action_searchResultsFragment_to_bookSearchResult, bundle)
    }

    companion object{
        fun init(searchTerm: String): SearchResultsFragment {
            return SearchResultsFragment().apply {
                arguments = bundleOf(SEARCH_TERM to searchTerm)
            }
        }
    }
}