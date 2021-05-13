package com.example.libproj2.ui.fragments

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libproj2.GlobalViewModel
import com.example.libproj2.R
import com.example.libproj2.models.BookCopy
import com.example.libproj2.models.BookInfo
import com.example.libproj2.repositories.BookCopiesRepository
import com.example.libproj2.repositories.ReaderBookOnHandsRepository
import com.example.libproj2.services.BookNetWebAppService
import com.example.libproj2.services.ImageLoadService
import com.example.libproj2.ui.fragments.books_history.BooksHistoryAdapter
import com.example.libproj2.ui.fragments.books_on_hand.BooksOnHandAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable

class ReaderFoundFragment : BaseMainActivityFragment(R.layout.fragment_reader_found) {
    val globalViewModel : GlobalViewModel by activityViewModels()
    val readerBookOnHandsRepository = ReaderBookOnHandsRepository.get()
    val bookCopiesRepository = BookCopiesRepository.get()
    val imageLoadService = ImageLoadService()
    val bookNetWebAppService = BookNetWebAppService.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = globalViewModel.selectedReaderUser!!
        readerBookOnHandsRepository.load(user.id)

        val user_name_textview = view.findViewById<TextView>(R.id.user_name_textview)
        user_name_textview.text = user.name

        val library_card_number_textview = view.findViewById<TextView>(R.id.library_card_number_textview)
        library_card_number_textview.text = user.barcode

        val recyclerview = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())

        val booksLiveData = readerBookOnHandsRepository.booksLiveData
        booksLiveData.observe(viewLifecycleOwner,
                {
                    recyclerview.adapter = BooksOnHandAdapter(it, ::loadImage, ::onItemClick)
                })

        val add_book_barcode_button = view.findViewById<Button>(R.id.add_book_barcode_button)
        add_book_barcode_button.setOnClickListener {
            scanBarcode()
        }

        val add_book_nfc_button = view.findViewById<Button>(R.id.add_book_nfc_button)
        add_book_nfc_button.setOnClickListener {
            findNavController().navigate(R.id.action_readerFoundFragment_to_nfcFragment3)
        }
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
                    findNavController().navigate(R.id.bookDetailsToTakeFragment3, bundle)
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

    fun searchInBookOnHandsRepository(barcode: String): Boolean{
        val bookCopy = readerBookOnHandsRepository.books.firstOrNull {
            c -> c.barcode == barcode
        }
        if(bookCopy != null){
            Snackbar.make(requireView(), "Вы уже взяли эту книгу", Snackbar.LENGTH_SHORT).show()
            return true
        }

        return false
    }

    fun loadImage(path: String): Drawable?{
        return imageLoadService.loadImage(path, requireContext())
    }

    fun onItemClick(bookCopy: BookCopy){
        val bundle = bundleOf("BOOKID" to bookCopy.id)
        findNavController().navigate(R.id.action_readerFoundFragment_to_bookDetailsTakenFragment3, bundle)
    }
}