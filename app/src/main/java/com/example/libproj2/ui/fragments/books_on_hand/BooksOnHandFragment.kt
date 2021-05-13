package com.example.libproj2.ui.fragments.books_on_hand

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libproj2.R
import com.example.libproj2.models.BookCopy
import com.example.libproj2.repositories.BookCopiesRepository
import com.example.libproj2.repositories.BooksOnHandRepository
import com.example.libproj2.services.BookNetWebAppService
import com.example.libproj2.services.ImageLoadService
import com.example.libproj2.ui.fragments.BookDetailsTakenFragment
import com.example.libproj2.ui.fragments.BookDetailsToTakeFragment
import com.example.libproj2.ui.fragments.BaseMainActivityFragment
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable

class BooksOnHandFragment : BaseMainActivityFragment(R.layout.fragment_books_on_hand) {
    val booksOnHandRepository = BooksOnHandRepository.get()
    val bookCopiesRepository = BookCopiesRepository.get()
    val imageLoadService = ImageLoadService()
    val bookNetWebAppService = BookNetWebAppService.get()

    override var toolbarTitle: String? = "Книги на руках"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = BooksOnHandAdapter(booksOnHandRepository.books, ::loadImage, ::onItemClick)

        val add_by_barcode_button = view.findViewById<Button>(R.id.add_by_barcode_button)
        add_by_barcode_button.setOnClickListener {
            scanBarcode()
        }

        val add_by_rfid_button = view.findViewById<Button>(R.id.add_by_rfid_button)
        add_by_rfid_button.setOnClickListener {
            findNavController().navigate(R.id.action_booksOnHandFragment_to_nfcFragment2)
        }
    }

    fun loadImage(path: String): Drawable?{
        return imageLoadService.loadImage(path, requireContext())
    }

    fun onItemClick(bookCopy: BookCopy){
        val bundle = bundleOf("BOOKID" to bookCopy.id)
        findNavController().navigate(R.id.action_booksOnHandFragment_to_bookDetailsTakenFragment, bundle)
    }

    fun scanBarcode(){
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
        integrator.setCameraId(0) // Use a specific camera of the device
        integrator.setOrientationLocked(false)
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(data != null){
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if(result != null){
                Log.d("MYTAG", result.contents)
                val bookOnHands = searchInBookOnHandsRepository(result.contents)
                if(bookOnHands == false) {
                    searchInAllBookCopies(result.contents)
                }
            }
        }
    }

    fun searchInAllBookCopies(barcode: String){
        val observer = object : SingleObserver<BookCopy> {
            override fun onSubscribe(d: Disposable?) {
//                TODO("Not yet implemented")
            }

            override fun onSuccess(t: BookCopy?) {
//                TODO("Not yet implemented")
                if(t != null){
                    bookCopiesRepository.add(t)
                    val bundle = bundleOf("BOOKID" to t.id)
                    findNavController().navigate(R.id.bookDetailsToTakeFragment, bundle)
                }
                else{
                    Snackbar.make(requireView(), "Книга не найдена", Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun onError(e: Throwable?) {
//                TODO("Not yet implemented")
                Snackbar.make(requireView(), "Ошибка", Snackbar.LENGTH_SHORT).show()
            }
        }

        val request = bookNetWebAppService.searchBookCopyByBarcode(barcode)
        request
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }

    fun searchInBookOnHandsRepository(barcode: String): Boolean{
        val bookCopy = booksOnHandRepository.books.firstOrNull {
            c -> c.barcode == barcode
        }
        if(bookCopy != null){
            val bookDetailsTakenFragment = BookDetailsTakenFragment.init(bookCopy.id)
//            goToFragment(bookDetailsTakenFragment)
            Snackbar.make(requireView(), "Вы уже взяли эту книгу", Snackbar.LENGTH_SHORT).show()
            return true
        }

        return false
    }
}