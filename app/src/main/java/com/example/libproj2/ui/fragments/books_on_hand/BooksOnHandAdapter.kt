package com.example.libproj2.ui.fragments.books_on_hand

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.libproj2.R
import com.example.libproj2.models.BookCopy
import com.example.libproj2.utils.toDateString

class BooksOnHandAdapter(
    val books: List<BookCopy>,
    val loadImage: (path: String) -> Drawable?,
    val onItemClick: (bookCopy: BookCopy) -> Unit
):
    RecyclerView.Adapter<BooksOnHandAdapter.BooksOnHandHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BooksOnHandHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_books_on_hand, parent, false)
        return BooksOnHandHolder(view)
    }

    override fun onBindViewHolder(holder: BooksOnHandHolder, position: Int) {
        if(books.size > position){
            holder.bind(books[position])
        }
    }

    override fun getItemCount(): Int = books.size

    inner class BooksOnHandHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(bookCopy: BookCopy){
            val author_textview = itemView.findViewById<TextView>(R.id.author_textview)
            val title_textview = itemView.findViewById<TextView>(R.id.title_textview)
            val return_date_textview = itemView.findViewById<TextView>(R.id.return_date_textview)
            val imageView = itemView.findViewById<ImageView>(R.id.imageView)
            val item_recyclerview = itemView as LinearLayout

            author_textview.text = bookCopy.author
            title_textview.text = bookCopy.title
            return_date_textview.text = "Вернуть до ${bookCopy.returnDate?.toDateString()}"

            imageView.setImageResource(R.drawable.book_no_image)
            bookCopy.bookInfo.imagePath?.let{
                val drawable = loadImage(it)
                drawable?.let {
                    imageView.setImageDrawable(drawable)
                }
            }

            item_recyclerview.setOnClickListener {
                onItemClick(bookCopy)
            }
        }
    }
}