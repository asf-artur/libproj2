package com.example.libproj2.ui.fragments.books_history

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.libproj2.R
import com.example.libproj2.models.BookInfo

class BooksHistoryAdapter(
        val books: List<BookInfo>,
        val loadImage: (path: String) -> Drawable?,
        val onItemClick: (bookInfo: BookInfo) -> Unit
):
        RecyclerView.Adapter<BooksHistoryAdapter.BooksHistoryHolder>(){
    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): BooksHistoryHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_books_on_hand1, parent, false)
        return BooksHistoryHolder(view)
    }

    override fun onBindViewHolder(holder: BooksHistoryHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int = books.size

    inner class BooksHistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(bookInfo: BookInfo){
            val author_textview = itemView.findViewById<TextView>(R.id.author_textview)
            val title_textview = itemView.findViewById<TextView>(R.id.title_textview)
            val return_date_textview = itemView.findViewById<TextView>(R.id.return_date_textview)
            val imageView = itemView.findViewById<ImageView>(R.id.imageView)
            val item_recyclerview = itemView as LinearLayout

            author_textview.text = bookInfo.authors?.get(0) ?: "автор не указан"
            title_textview.text = bookInfo.title
            return_date_textview.visibility = View.GONE

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