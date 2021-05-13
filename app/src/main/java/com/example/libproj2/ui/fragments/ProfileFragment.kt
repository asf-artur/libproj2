package com.example.libproj2.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.libproj2.GlobalViewModel
import com.example.libproj2.MainApp
import com.example.libproj2.R
import com.example.libproj2.models.BookCopy
import com.example.libproj2.models.User
import com.example.libproj2.models.UserCategory
import com.example.libproj2.repositories.AllBookCopiesRepository
import com.example.libproj2.repositories.BookCopiesRepository
import com.example.libproj2.repositories.BooksOnHandRepository
import com.example.libproj2.repositories.UserRepository
import com.example.libproj2.services.BookNetWebAppService
import com.example.libproj2.services.ImageLoadService
import com.example.libproj2.ui.fragments.all_books.AllBooksFragment
import com.example.libproj2.ui.fragments.books_history.BooksHistoryFragment
import com.example.libproj2.ui.fragments.books_on_hand.BooksOnHandFragment
import com.example.libproj2.utils.SystemServicesStorage
import com.example.libproj2.utils.ToolbarOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.zxing.integration.android.IntentIntegrator
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject


class ProfileFragment: BaseMainActivityFragment(R.layout.fragment_profile) {
    val globalViewModel: GlobalViewModel by activityViewModels()
    lateinit var user: User
    val imageLoadService = ImageLoadService()
    @Inject lateinit var userRepository: UserRepository
    val booksOnHandRepository = BooksOnHandRepository.get()
    val bookCopiesRepository = BookCopiesRepository.get()
    val bookNetWebAppService = BookNetWebAppService.get()

    override var toolbarTitle: String? = "Библиотека"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        user = globalViewModel.activeUser
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainApp.component.inject(this)

        val userliveData = userRepository.userLiveData
        userliveData.observe(
            viewLifecycleOwner,
            {
                it?.let{
                    user = it

                    val mainLinearLayout = view.findViewById<LinearLayout>(R.id.mainLinearLayout)
                    mainLinearLayout.setOnFocusChangeListener { v, hasFocus ->
                        if(hasFocus){
                            val systemServicesStorage = SystemServicesStorage.get()
                            systemServicesStorage.inputMethodManager.hideSoftInputFromWindow(mainLinearLayout.windowToken, 0)
                        }
                    }

                    val user_barcode_imageView = view.findViewById<ImageView>(R.id.user_barcode_imageView)
                    user.barcode?.let {
                        imageLoadService.setBarcodeImage(it, user_barcode_imageView)
                    }

                    val name_textView = view.findViewById<TextView>(R.id.name_textView)
                    name_textView.text = user.name

                    val barcode_number_textView = view.findViewById<TextView>(R.id.barcode_number_textView)
                    barcode_number_textView.text = user.barcode ?: ""

                    val books_on_hand_button = view.findViewById<Button>(R.id.books_on_hand_button)
                    books_on_hand_button.setOnClickListener {
                        findNavController().navigate(R.id.action_profileFragment_to_booksOnHandFragment)
                    }

                    val library_card_button = view.findViewById<Button>(R.id.library_card_button)
                    library_card_button.setOnClickListener {
                        findNavController().navigate(R.id.action_profileFragment_to_libraryCardFragment)
                    }

                    val books_history_button = view.findViewById<Button>(R.id.books_history_button)
                    books_history_button.setOnClickListener {
                        findNavController().navigate(R.id.action_profileFragment_to_booksHistoryFragment)
                    }

                    val barcode_scan_button = view.findViewById<Button>(R.id.barcode_scan_button)
                    barcode_scan_button.setOnClickListener {
                        scanBarcode()
                    }
                }
            }
        )
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

    fun searchInBookOnHandsRepository(barcode: String): Boolean{
        val bookCopy = booksOnHandRepository.books.firstOrNull {
            c -> c.barcode == barcode
        }
        if(bookCopy != null){
            val bundle = bundleOf("BOOKID" to bookCopy.id)
            findNavController().navigate(R.id.bookDetailsTakenFragment, bundle)
            Snackbar.make(requireView(), "Вы уже взяли эту книгу", Snackbar.LENGTH_SHORT).show()
            return true
        }

        return false
    }

    fun searchInAllBookCopies(barcode: String){
        val observer = object : SingleObserver<BookCopy>{
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null){
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

}