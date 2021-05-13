package com.example.libproj2.ui.fragments

import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.nfc.NfcManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libproj2.GlobalViewModel
import com.example.libproj2.MainActivity
import com.example.libproj2.MainApp
import com.example.libproj2.R
import com.example.libproj2.models.BookInfo
import com.example.libproj2.models.User
import com.example.libproj2.repositories.BooksHistoryRepository
import com.example.libproj2.repositories.BooksOnHandRepository
import com.example.libproj2.repositories.UserRepository
import com.example.libproj2.services.BookNetWebAppService
import com.example.libproj2.services.ImageLoadService
import com.example.libproj2.ui.fragments.books_history.BooksHistoryAdapter
import com.example.libproj2.utils.ToolbarOptions
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class LibraryCardFragment : BaseMainActivityFragment(R.layout.fragment_library_card) {
    val bookNetWebAppService = BookNetWebAppService.get()
    @Inject lateinit var userRepository: UserRepository
    lateinit var user : User
    val imageLoadService = ImageLoadService()
    val booksHistoryRepository = BooksHistoryRepository.get()
    val booksOnHandRepository = BooksOnHandRepository.get()
    val globalViewModel: GlobalViewModel by activityViewModels()

    override var toolbarTitle: String? = "Читательский билет"
    override var toolbarOptions: ToolbarOptions = ToolbarOptions(R.menu.top_menu_profile_fragment) {
        when(it.itemId){
            R.id.logout -> {
                val al = AlertDialog.Builder(requireContext())
                        .setIcon(R.drawable.library_icon)
                        .setMessage("Вы уверены, что хотите выйти из аккаунта?")
                        .setNegativeButton("Отмена"){ dialog: DialogInterface?, which: Int ->
                            dialog!!.dismiss()
                        }
                        .setPositiveButton("Ок", object : DialogInterface.OnClickListener{
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                val g = bookNetWebAppService.logoutRx()

                                val handler = Handler(Looper.getMainLooper())

                                val onSuccess = Consumer<Unit>(){
                                    userRepository.logout()
                                    val act = requireActivity() as MainActivity
                                    act.mainNavHostFragments.forEach {
                                        handler.post {
                                            if(it.value.lifecycle.currentState > Lifecycle.State.STARTED){
                                                val navController = it.value.findNavController()
                                                navController.popBackStack(navController.graph.startDestinationId, false)
                                            }
                                        }
                                    }
                                }
                                g.subscribe(onSuccess)
                            }
                        })
                        .show()

                true
            }
            else -> false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainApp.component.inject(this)

        val userLiveData = userRepository.userLiveData
        user = userLiveData.value!!
        if(globalViewModel.selectedReaderUser != null){
            user = globalViewModel.selectedReaderUser!!
            globalViewModel.selectedReaderUser = null
        }
        val nfc = requireContext().getSystemService(Context.NFC_SERVICE) as NfcManager
        val nfcAdapter = nfc.defaultAdapter
        val nfc_textview = view.findViewById<TextView>(R.id.nfc_textview)
        if(nfcAdapter != null && nfcAdapter.isEnabled){
            nfc_textview.text = getString(R.string.nfc_enabled)
        }
        else if(nfcAdapter != null && !nfcAdapter.isEnabled){
            nfc_textview.text = getString(R.string.nfc_not_enabled)
        }
        else{
            nfc_textview.text = getString(R.string.nfc_not_supported)
        }

        val imageView = view.findViewById<ImageView>(R.id.imageView)
        imageView.setImageResource(R.drawable.profile_no_image)
        user.imagePath?.let {
            val drawable = imageLoadService.loadImage(it, requireContext())
            imageView.setImageDrawable(drawable)
        }

        val user_name_textview = view.findViewById<TextView>(R.id.user_name_textview)
        user_name_textview.text = user.name

        val library_card_number_textview = view.findViewById<TextView>(R.id.library_card_number_textview)
        library_card_number_textview.text = getString(R.string.librarycard_barcode, user.barcode)

        val books_on_hand_textview = view.findViewById<TextView>(R.id.books_on_hand_textview)
        books_on_hand_textview.text = getString(R.string.books_on_hand, booksOnHandRepository.books.size.toString())

        val books_history_textview = view.findViewById<TextView>(R.id.books_history_textview)
        books_history_textview.text = getString(R.string.books_history, booksHistoryRepository.books.size.toString())

        val recyclerview = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.adapter = BooksHistoryAdapter(booksHistoryRepository.books, ::loadImage, ::onItemClick)
//        userLiveData.observe(
//            viewLifecycleOwner,
//            {
//                it?.let{
//                    user = it
//                }
//            }
//        )
    }

    fun loadImage(path: String): Drawable?{
        return imageLoadService.loadImage(path, requireContext())
    }

    fun onItemClick(bookInfo: BookInfo){
        val bundle = bundleOf("BOOKID" to bookInfo.id)
        findNavController().navigate(R.id.action_libraryCardFragment_to_bookDetailsHistoryFragment, bundle)
    }
}