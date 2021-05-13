package com.example.libproj2.ui.fragments.catalog

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

class CatalogAdapter(
    val books: List<BookCopy>,
    val loadImage: (path: String) -> Drawable?,
    val onItemClick: (bookCopy: BookCopy) -> Unit
):
    RecyclerView.Adapter<CatalogAdapter.CatalogHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CatalogHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_books_on_hand, parent, false)
        return CatalogHolder(view)
    }

    override fun onBindViewHolder(holder: CatalogHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int = books.size

    inner class CatalogHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
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
                imageView.setImageDrawable(drawable)
            }

            item_recyclerview.setOnClickListener {
                onItemClick(bookCopy)
            }
        }
    }
}