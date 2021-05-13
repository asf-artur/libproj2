package com.example.libproj2.ui.fragments.catalog

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.*
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.widget.SwitchCompat
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.core.view.get
import androidx.core.view.size
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libproj2.GlobalViewModel
import com.example.libproj2.MainApp
import com.example.libproj2.R
import com.example.libproj2.models.*
import com.example.libproj2.repositories.*
import com.example.libproj2.retrofit.contracts.*
import com.example.libproj2.services.BookNetSearchService
import com.example.libproj2.services.BookNetWebAppService
import com.example.libproj2.services.ImageLoadService
import com.example.libproj2.ui.fragments.BaseMainActivityFragment
import com.example.libproj2.ui.fragments.search_results.SearchResultsAdapter
import com.example.libproj2.utils.SystemServicesStorage
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class CatalogFragment : BaseMainActivityFragment(R.layout.fragment_catalog) {
    val globalViewModel : GlobalViewModel by activityViewModels()
    val searchSources = lazy { globalViewModel.searchSources }

    val bookCopiesRepository = BookCopiesRepository.get()
    val imageLoadService = ImageLoadService()
    val bookSearchResultRepository = BookSearchResultRepository.get()
    val allBookInfoRepository = AllBookInfoRepository.get()
    val allBookCopiesRepository = AllBookCopiesRepository.get()
    @Inject lateinit var userRepository: UserRepository

    override var toolbarTitle: String? = "Поиск по каталогу"
    lateinit var textInput: TextInputEditText
    val bookNetSearchService = BookNetSearchService()
    lateinit var progressbar: ProgressBar
    val bookNetWebAppService = BookNetWebAppService.get()
    var nextId = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainApp.component.inject(this)

        val user = userRepository.userLiveData.value!!

        val mainLinearLayout = view.findViewById<LinearLayout>(R.id.mainLinearLayout)
        mainLinearLayout.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                val systemServicesStorage = SystemServicesStorage.get()
                systemServicesStorage.inputMethodManager.hideSoftInputFromWindow(mainLinearLayout.windowToken, 0)
            }
        }

        val searchLinearLayout = view.findViewById<LinearLayout>(R.id.search_layout)
        searchLinearLayout.visibility = View.VISIBLE

        val navigationView = view.findViewById<NavigationView>(R.id.navigationView)
        val menuItems = (0 until navigationView.menu.size)
                .map { navigationView.menu.getItem(it).actionView.findViewById<SwitchCompat>(R.id.switch_item) }
        val fItem = navigationView.menu.getItem(0).actionView.findViewById<SwitchCompat>(R.id.switch_item)
        fItem.isChecked = true
        fItem.setOnCheckedChangeListener { buttonView, isChecked ->
            searchSources.value[SearchSourceType.LibraryBookInfo] = isChecked
            if(isChecked){
                menuItems[2].isChecked = false
            }
        }
        val sItem = navigationView.menu.getItem(1).actionView.findViewById<SwitchCompat>(R.id.switch_item)
        sItem.setOnCheckedChangeListener { buttonView, isChecked ->
            searchSources.value[SearchSourceType.Google] = isChecked
            if(isChecked){
                menuItems[2].isChecked = false
            }
        }
        val thirdItem = navigationView.menu.getItem(2).actionView.findViewById<SwitchCompat>(R.id.switch_item)
        navigationView.menu.getItem(2).isVisible = user.userCategory == UserCategory.Librarian || user.userCategory == UserCategory.Admin
        thirdItem.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                searchSources.value[SearchSourceType.LibraryBookCopy] = isChecked
                menuItems.forEach { c -> if(c != thirdItem) c.isChecked = false }
            }
            else{
                searchSources.value[SearchSourceType.LibraryBookCopy] = isChecked
            }
        }

        navigationView.setNavigationItemSelectedListener {
            val sw = it.actionView.findViewById<SwitchCompat>(R.id.switch_item)
            sw.isChecked = !sw.isChecked
            true
        }

        val drawer_layout = view.findViewById<DrawerLayout>(R.id.drawer_layout)
        val search_filter_button = searchLinearLayout.findViewById<ImageButton>(R.id.search_filter_button)
        search_filter_button.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.END)
//            findNavController().navigate(R.id.action_catalogFragment_to_searchFiltersFragment)
        }

        textInput = searchLinearLayout.findViewById<TextInputEditText>(R.id.textinput)
        val textInputLayout = searchLinearLayout.findViewById<TextInputLayout>(R.id.textInputLayout)
        textInputLayout.setEndIconOnClickListener {
            textInput.text?.clear()
            textInput.clearFocus()
            val systemServicesStorage = SystemServicesStorage.get()
            systemServicesStorage.inputMethodManager.hideSoftInputFromWindow(searchLinearLayout.windowToken, 0)
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

//        searchClickWithImages("цветы", view, recyclerView)
        recyclerView.adapter = SearchResultsAdapter(bookSearchResultRepository.books, ::loadImage, ::onItemClick)

        textInput.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE){
                bookSearchResultRepository.clear()
//                getSelectedSearchSources(navigationView)
                searchClickWithImages(v.text.toString(), view, recyclerView)
                true
            }
            false
        }
    }

    fun searchClickWithImages(searchTerm : String, view: View, recyclerView: RecyclerView) {
        progressbar = view.findViewById(R.id.progressbar)
        val handler = Handler(requireContext().mainLooper)

        val observer = object : Observer<BookInfoFromSearch>{
            override fun onSubscribe(d: Disposable?) {
//                TODO("Not yet implemented")
            }

            override fun onNext(t: BookInfoFromSearch?) {
//                TODO("Not yet implemented")
                bookSearchResultRepository.add(t!!)
            }

            override fun onError(e: Throwable?) {
//                TODO("Not yet implemented")
            }

            override fun onComplete() {
//                TODO("Not yet implemented")
            }

        }

        var merged = Observable.empty<List<BookInfoFromSearch>>()
                .doOnSubscribe {
                    handler.post {
                        progressbar.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                }

        var result = bookNetSearchService.searchInGoogleBooksRx(searchTerm)
                .map {
                    it.items.map {
                        nextId++
                        val res = it.toBookInfoFromSearch(nextId)
                        return@map res
                    }
                }
                .toObservable()

        var searchBookInfoRx = bookNetWebAppService.searchBookInfoRx(searchTerm, nextId)
//                .doOnSubscribe {
//                    handler.post {
//                        progressbar.visibility = View.VISIBLE
//                        recyclerView.visibility = View.GONE
//                    }
//                }
        if(searchSources.value[SearchSourceType.LibraryBookInfo] == true){
            merged = merged
                    .mergeWith(searchBookInfoRx)
        }
        if(searchSources.value[SearchSourceType.Google] == true){
            merged = merged
                    .mergeWith(result)
        }
        if(searchSources.value[SearchSourceType.LibraryBookCopy] == true){
            merged = merged.mergeWith(bookNetWebAppService.searchBookCopy(searchTerm, nextId))
        }
        merged
                .concatMap {
                    Observable.fromIterable(it)
                }
                .concatMap {
                    if(it.externalImagePath != null){
                        if(it.bookInfoSource == BookInfoSource.LocalCatalogBookInfo){
                            Observable.just(it)
                                    .zipWith(bookNetWebAppService.getBookInfoImageRx(it.externalImagePath, requireContext()).toObservable(), { t1, t2 ->
                                        val bitmap = t2!!.byteStream().use(BitmapFactory::decodeStream)
                                        val externalCacheFile = File(requireContext().externalCacheDir, it.imagePath!!)
                                        val stream = FileOutputStream(externalCacheFile)
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                                        stream.flush()
                                        stream.close()
                                        t1
                                    })
                        }
                        else if(it.bookInfoSource == BookInfoSource.Google){
                            Observable.just(it)
                                    .zipWith(bookNetWebAppService.getGoogleBookInfoImageRx(it.externalImagePath).toObservable(), { t1, t2 ->
                                        val bitmap = t2!!.byteStream().use(BitmapFactory::decodeStream)
                                        val externalCacheFile = File(requireContext().externalCacheDir, it.imagePath!!)
                                        val stream = FileOutputStream(externalCacheFile)
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                                        stream.flush()
                                        stream.close()
                                        t1
                                    })
                        }
                        else{
                            Observable.just(it)
                        }
                    }
                   else{
                        Observable.just(it)
                    }
                }
                .doOnComplete {
                    Log.d("MYTAG", "------------------------------------")
                    handler.post {
                        val list = bookSearchResultRepository.books
                        recyclerView.adapter = SearchResultsAdapter(list, ::loadImage, ::onItemClick)
                        recyclerView.visibility = View.VISIBLE
                        progressbar.visibility = View.GONE
                    }
                }
                .subscribe(observer)

    }

    fun searchClick(searchTerm : String, view: View, recyclerView: RecyclerView){
        Log.d("MYTAG", "clicked")
        val observerN = object : Observer<List<BookInfoFromSearch>>{
            private var disp : Disposable? = null

            override fun onSubscribe(d: Disposable?) {
//                TODO("Not yet implemented")
                disp = d
            }

            override fun onNext(t: List<BookInfoFromSearch>?) {
//                TODO("Not yet implemented")

                t!!.forEach {
                    bookSearchResultRepository.add(it)
                }
            }

            override fun onError(e: Throwable?) {
                Log.d("MYTAG", "error")
//                TODO("Not yet implemented")
            }

            override fun onComplete() {
//                TODO("Not yet implemented")
            }
        }
        progressbar = view.findViewById(R.id.progressbar)
        val handler = Handler(requireContext().mainLooper)

        var merged = Observable.empty<List<BookInfoFromSearch>>()
                .doOnSubscribe {
                    handler.post {
                        progressbar.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                }
        var result = bookNetSearchService.searchInGoogleBooksRx(searchTerm)
                .map {
                    it.items.map {
                        nextId++
                        return@map it.toBookInfoFromSearch(nextId)
                    }
                }
                .toObservable()
        if(searchSources.value[SearchSourceType.LibraryBookInfo] == true){
            merged = merged.mergeWith(bookNetWebAppService.getAllBookInfoSearchRx(nextId))
        }
        if(searchSources.value[SearchSourceType.Google] == true){
            merged = merged.mergeWith(result)
        }
        if(searchSources.value[SearchSourceType.LibraryBookCopy] == true){
            merged = merged.mergeWith(bookNetWebAppService.getAllBookCopyCatalogRx(nextId))
        }
        merged
                .onErrorComplete()
                .doOnComplete {
                    Log.d("MYTAG", "------------------------------------")
                    handler.post {
                        val list = bookSearchResultRepository.books
                        recyclerView.adapter = SearchResultsAdapter(list, ::loadImage, ::onItemClick)
                        recyclerView.visibility = View.VISIBLE
                        progressbar.visibility = View.GONE
                    }
                }
                .subscribe(observerN)
    }

    fun loadImage(path: String): Drawable?{
        return imageLoadService.loadImage(path, requireContext())
    }

    fun onItemClick(bookInfoFromSearch: BookInfoFromSearch){
        val user = userRepository.userLiveData.value!!
        if((user.userCategory == UserCategory.Admin || user.userCategory == UserCategory.Librarian)&& bookInfoFromSearch.bookInfoSource == BookInfoSource.LocalCatalogBookCopy){
            val bookCopy = bookCopiesRepository.books.first { c -> c.id == bookInfoFromSearch.bookCopyId }
            if(bookCopy.bookStatus == BookStatus.NotInStock){
                val bundle = bundleOf("BOOKID" to bookCopy.id)
                findNavController().navigate(R.id.action_catalogFragment_to_bookDetailsTakenFragment2, bundle)
            }
            else{
                val bundle = bundleOf("BOOKID" to bookCopy.id)
                findNavController().navigate(R.id.action_catalogFragment_to_bookDetailsToTakeFragment2 , bundle)
            }
        }
        else{
            val bundle = bundleOf("BOOKID" to bookInfoFromSearch.id)
            findNavController().navigate(R.id.action_catalogFragment_to_bookSearchResult, bundle)
        }
    }
}