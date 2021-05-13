package com.example.libproj2.ui.fragments.search_results

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.libproj2.R
import com.example.libproj2.models.*
import com.example.libproj2.repositories.AllBookCopiesRepository
import com.example.libproj2.repositories.BookCopiesRepository

class SearchResultsAdapter(
    val books: List<BookInfoFromSearch>,
    val loadImage: (path: String) -> Drawable?,
    val onItemClick: (bookInfo: BookInfoFromSearch) -> Unit
):
    RecyclerView.Adapter<SearchResultsAdapter.SearchResultsHolder>(){
    val bookCopiesRepository = BookCopiesRepository.get()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchResultsHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_books_on_hand, parent, false)
        return SearchResultsHolder(view)
    }

    override fun onBindViewHolder(holder: SearchResultsHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int = books.size

    inner class SearchResultsHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(bookInfo: BookInfoFromSearch){
            val author_textview = itemView.findViewById<TextView>(R.id.author_textview)
            val title_textview = itemView.findViewById<TextView>(R.id.title_textview)
            val return_date_textview = itemView.findViewById<TextView>(R.id.return_date_textview)
            val imageView = itemView.findViewById<ImageView>(R.id.imageView)
            val item_recyclerview = itemView as LinearLayout

            author_textview.text = bookInfo.authors?.joinToString()
            title_textview.text = bookInfo.title
            return_date_textview.text = bookInfo.bookInfoSource.toRusString()
            if(bookInfo.bookInfoSource == BookInfoSource.LocalCatalogBookCopy){
                val bookCopy = bookCopiesRepository.books.first { c -> c.id == bookInfo.bookCopyId }
                return_date_textview.text = bookInfo.bookInfoSource.toRusString() + "\n" + bookCopy.barcode.toString()
            }

            imageView.setImageResource(R.drawable.book_no_image)
            bookInfo.imagePath?.let{
                val drawable = loadImage(it)
                drawable?.let{
                    imageView.setImageDrawable(drawable)
                }
            }

            item_recyclerview.setOnClickListener {
                onItemClick(bookInfo)
            }
        }
    }
}