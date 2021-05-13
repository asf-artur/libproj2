package com.example.libproj2

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.libproj2.di.A
import com.example.libproj2.di.DaggerAppComponent
import com.example.libproj2.models.*
import com.example.libproj2.repositories.*
import com.example.libproj2.services.BookNetWebAppService
import com.example.libproj2.services.ExampleBuilder
import com.example.libproj2.ui.fragments.BaseMainActivityFragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject

class MainActivity : AppCompatActivity(), BaseMainActivityFragment.ActivityCallback {
    val globalViewModel: GlobalViewModel by viewModels()
    val booksOnHandRepository = BooksOnHandRepository.get()
    val booksHistoryRepository = BooksHistoryRepository.get()
    val bookCopiesRepository = BookCopiesRepository.get()
    @Inject lateinit var inAppNotificationsRepository: InAppNotificationsRepository
    var oldInAppNotifications: MutableList<InAppNotification> = mutableListOf()
    lateinit var inAppNotificationsLiveData : MutableLiveData<List<InAppNotification>>
//    val userRepository = UserRepository.get()
    val sharedPreferencesRepository = SharedPreferencesRepository.get()
    var initialized = false
    @Inject lateinit var a : A
    @Inject lateinit var userRepositoryInjected: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MainApp.component.inject(this)

        val uuu = userRepositoryInjected.user
        inAppNotificationsLiveData = inAppNotificationsRepository.inAppNotificationsLiveDataVisible

        val tag: Tag? = intent?.getParcelableExtra(NfcAdapter.EXTRA_TAG)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener{ item ->
            when(item.itemId){
                R.id.page1 -> {
                    goToPage(R.navigation.profile)
                }
                R.id.page2 -> {
                    goToPage(R.navigation.catalog)
                }
                R.id.page3 -> {
                    goToPage(R.navigation.notifications)
                }
                R.id.page4 ->{
                    goToPage(R.navigation.reader)
                }
            }
            true
        }

        nfc()
        prepare()

        if(userRepositoryInjected.userLiveData.value == null){
            checkUserLogin1()
        }

        userRepositoryInjected.userLiveData.observe(this,
                {
                    if(it == null && initialized){
                        bottomNavigationView.visibility = View.GONE
                        if(!initialized){
                            supportFragmentManager.beginTransaction()
                                    .add(R.id.fragment_container, mainNavHostFragments[R.navigation.login]!!)
                                    .commit()
                        }
                        initialized = true
                        goToPage(R.navigation.login)
                    }
                })

        if(savedInstanceState == null){
            initMainProfileNavHost()
            userRepositoryInjected.userLiveData.observe(
                    this,
                    {
                        it?.let{
                            bottomNavigationView.visibility = View.VISIBLE
                            if(it.userCategory == UserCategory.Librarian || it.userCategory == UserCategory.Admin){
                                onUserLibrarian(bottomNavigationView)
                            }
                            globalViewModel.activeUser = it
                            if(!initialized || supportFragmentManager.fragments.isEmpty()) {
                                lastFragmentNum = R.navigation.profile
                                supportFragmentManager.beginTransaction()
                                        .add(R.id.fragment_container, mainNavHostFragments[R.navigation.profile]!!)
                                        .commitNow()
                            }

                            goToPage(R.navigation.profile)
                            if(tag != null){
                                onNewTag(tag)
                            }
//                            val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
//                            val navHostController = navHostFragment.navController
//                            goToPage(R.navigation.net_fragment)
                            initialized = true
                        }
                        if(it == null){
                            lastFragmentNum = R.navigation.login
                            bottomNavigationView.visibility = View.GONE
                            if(!initialized){
                                supportFragmentManager.beginTransaction()
                                        .add(R.id.fragment_container, mainNavHostFragments[R.navigation.login]!!)
                                        .commit()
                            }
                            initialized = true
                        }
                    }
            )
        }
    }


    val mainNavHostFragments: MutableMap<Int, Fragment> = mutableMapOf()
    var lastFragmentNum = R.navigation.profile

    fun initMainProfileNavHost(){
        mainNavHostFragments[R.navigation.profile] = NavHostFragment.create(R.navigation.profile)
        mainNavHostFragments[R.navigation.catalog] = NavHostFragment.create(R.navigation.catalog)
        mainNavHostFragments[R.navigation.notifications] = NavHostFragment.create(R.navigation.notifications)
        mainNavHostFragments[R.navigation.net_fragment] = NavHostFragment.create(R.navigation.net_fragment)
        mainNavHostFragments[R.navigation.login] = NavHostFragment.create(R.navigation.login)
        mainNavHostFragments[R.navigation.nfc] = NavHostFragment.create(R.navigation.nfc)
        mainNavHostFragments[R.navigation.reader] = NavHostFragment.create(R.navigation.reader)
    }

    lateinit var currentNavController: NavController
    fun goToPage(newFragmentNum: Int){
        if(newFragmentNum == lastFragmentNum){
            val navController = mainNavHostFragments[newFragmentNum]!!.findNavController()
            navController.popBackStack(navController.graph.startDestinationId, false)
            return
        }
        if(supportFragmentManager.fragments.contains(mainNavHostFragments[newFragmentNum])){
            supportFragmentManager.beginTransaction()
                    .hide(mainNavHostFragments[lastFragmentNum]!!)
                    .setMaxLifecycle(mainNavHostFragments[lastFragmentNum]!!, Lifecycle.State.STARTED)
                    .show(mainNavHostFragments[newFragmentNum]!!)
                    .setMaxLifecycle(mainNavHostFragments[newFragmentNum]!!, Lifecycle.State.RESUMED)
                    .commit()
        }
        else{
            supportFragmentManager.beginTransaction()
                    .hide(mainNavHostFragments[lastFragmentNum]!!)
                    .setMaxLifecycle(mainNavHostFragments[lastFragmentNum]!!, Lifecycle.State.STARTED)
                    .add(R.id.fragment_container, mainNavHostFragments[newFragmentNum]!!)
                    .commit()
        }

//        Log.d("MYTAG", "fragments: ${supportFragmentManager.fragments.size}")
        lastFragmentNum = newFragmentNum
    }

    override fun onBackPressed() {
        val navController = mainNavHostFragments[lastFragmentNum]!!.findNavController()
        val isNavigatedUp = navController.navigateUp()
        if(isNavigatedUp){
            return
        }
        else{
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        val cookieRepository = CookieRepository.get()
        sharedPreferencesRepository.setSharedPreference(this, SharedPreferencesRepository.UserIdentityCookiesKey, cookieRepository.toJsonString())

        getSharedPreferences("default", Context.MODE_PRIVATE)
                .edit()
                .putString(SharedPreferencesRepository.UserIdentityCookiesKey, cookieRepository.toJsonString())
                .apply()
    }

    fun onUserLibrarian(bottomNavigationView: BottomNavigationView){
        val readerBottomMenuItem = (0 until bottomNavigationView.menu.size)
                .map {
                    bottomNavigationView.menu.getItem(it)
                }
                .first{ c -> c.itemId == R.id.page4 }

        readerBottomMenuItem.isVisible = true
        readerBottomMenuItem.isEnabled = true
    }

    var mAdapter: NfcAdapter? = null
    lateinit var mPendingIntent: PendingIntent
    fun nfc(){
        mAdapter = NfcAdapter.getDefaultAdapter(this)
        mPendingIntent = PendingIntent.getActivity(
                this, 0, Intent(
                this,
                javaClass
        ).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )
        globalViewModel.mAdapter = mAdapter
        globalViewModel.mPendingIntent = mPendingIntent
    }


    fun prepare(){
        val exampleBuilder = ExampleBuilder()

        var inAppNotifications = mutableListOf<InAppNotification>(
            BookNotification(
                0,
                "Попытка взять книгу",
                0,
                0,
                Calendar.getInstance(),
                InAppNotificationType.TryBorrowBook
            ),
            BookNotification(
                1,
                "Попытка взять книгу",
                11,
                111,
                Calendar.getInstance(),
                InAppNotificationType.TryBorrowBookRejected
            ),
            BookNotification(
                2,
                "Попытка взять книгу",
                11,
                11,
                Calendar.getInstance(),
                InAppNotificationType.BookIsBorrowed
            )
        )

        val generateNewNotification = { c: Int -> BookNotification(c, "title", 11, 11, Calendar.getInstance(), InAppNotificationType.TryBorrowBook) }


        inAppNotificationsLiveData.observe(
            this,
            {
                it?.let{
//                    val count = it.count { c -> c.inAppNotificationType == InAppNotificationType.TryBorrowBook }
                    val count = it.count { c -> !c.isRead }
                    val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                    val badge = bottomNavigationView.getOrCreateBadge(R.id.page3)
                    if(count > 0){
                        badge.isVisible = true
                        badge.number = count
                    }
                    else{
                        badge.isVisible = false
                    }
                    oldInAppNotifications = it.toMutableList()
                }
            }
        )
    }

    fun checkUserLogin1() : LiveData<User>{
        val result: MutableLiveData<User> = MutableLiveData()

        val bookNetWebAppService = BookNetWebAppService.get()

        val observer = object : SingleObserver<User> {
            override fun onSubscribe(d: Disposable?) {
            }

            override fun onSuccess(t: User?) {
                result.postValue(t)
                t?.let{
                    userRepositoryInjected.add(it)
                }
                Log.d("MYTAG", "onSuccess")
            }

            override fun onError(e: Throwable?) {
                userRepositoryInjected.userLiveData.postValue(null)
                Log.d("MYTAG", "onError")
            }

        }


        val userObservable = bookNetWebAppService.checkLoginRx()
        userObservable
            .subscribeOn(Schedulers.newThread())
                .doOnSuccess {
                    bookNetWebAppService.sendClientToken("ttooken")
                }
                .subscribe(observer)

        return result
    }

    var done = false
    fun checkUserLogin(): User?{
        val exampleBuilder = ExampleBuilder()
        val allUserLogins = exampleBuilder.buildUserLogins()

        val bookNetWebAppService = BookNetWebAppService.get()

//        bookNetWebAppService.getAllBookCopy()

        val s = userRepositoryInjected.get()

        s.observe(
            this,
            {
                if(it != null && false){
                    Log.d("MYTAG", it.name)
                    userRepositoryInjected.userLiveData.postValue(it)
                }
                else{
                    Log.d("MYTAG", "user == null")
                    val userNullabe = bookNetWebAppService.checkLogin()

                    userNullabe.observe(
                        this,
                        {
                            it?.let{
                                userRepositoryInjected.add(it)
                                userRepositoryInjected.userLiveData.postValue(it)
                                done = true
                            }
                        }
                    )
                }
            }
        )

        val user = exampleBuilder.buildMe(UserCategory.Admin)
        return user
    }

    override fun goToFragmentCallback(fragment: Fragment, addToBackStack: Boolean){
        goToFragment(fragment, addToBackStack)
    }

    private fun goToFragment(fragment: Fragment, addToBackStack: Boolean){
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .apply {
                    if(addToBackStack) addToBackStack(fragment::class.java.simpleName)
                }
                .commit()
    }

    fun goToFragmentAndClearBackStack(fragment: Fragment){
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        goToFragment(fragment, addToBackStack = false)
    }

    fun onNewTag(tag : Tag?){
        val bookNetWebAppService = BookNetWebAppService.get()

        tag?.let{
            val rfid = it.id.toList().toString()

            if(globalViewModel.selectedReaderUser == null){
                val bookOnHands = searchInBookOnHandsRepository(rfid)
                if(bookOnHands == false){

                    val observer = object : SingleObserver<BookCopy>{
                        override fun onSubscribe(d: Disposable?) {
//                    TODO("Not yet implemented")
                        }

                        override fun onSuccess(t: BookCopy?) {
                            t?.let {
                                bookCopiesRepository.add(t)
                                val bundle = bundleOf("BOOKID" to t.id)
                                val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
                                var navHostController = navHostFragment.navController
                                navHostController = mainNavHostFragments[lastFragmentNum]!!.findNavController()
                                navHostController.navigate(R.id.bookDetailsToTakeFragment, bundle)
                            }
                        }

                        override fun onError(e: Throwable?) {
//                    TODO("Not yet implemented")
                        }
                    }

                    val req = bookNetWebAppService.searchBookCopyByRfid(rfid)
                    req
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(observer)
                }
            }
            else{
                val observer = object : SingleObserver<BookCopy>{
                    override fun onSubscribe(d: Disposable?) {
//                    TODO("Not yet implemented")
                    }

                    override fun onSuccess(t: BookCopy?) {
                        t?.let {
                            if(t.bookStatus == BookStatus.NotInStock){
                                val view = findViewById<FragmentContainerView>(R.id.fragment_container)
                                Snackbar.make(view, "Эту книгу нельзя добавить, она уже взята кем-то другим", Snackbar.LENGTH_SHORT).show()
                            }
                            else{
                                bookCopiesRepository.add(t)
                                val bundle = bundleOf("BOOKID" to t.id)
                                val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
                                var navHostController = navHostFragment.navController
                                navHostController = mainNavHostFragments[lastFragmentNum]!!.findNavController()
                                navHostController.navigate(R.id.bookDetailsToTakeFragment3, bundle)
                            }
                        }
                    }

                    override fun onError(e: Throwable?) {
//                    TODO("Not yet implemented")
                    }
                }

                val req = bookNetWebAppService.searchBookCopyByRfid(rfid)
                req
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(observer)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val tag: Tag? = intent?.getParcelableExtra(NfcAdapter.EXTRA_TAG)

        onNewTag(tag)
    }

    fun searchInBookOnHandsRepository(rfid: String): Boolean{
        val bookCopy = booksOnHandRepository.books.firstOrNull {
            c -> c.rfidId == rfid
        }
        if(bookCopy != null){
            val bundle = bundleOf("BOOKID" to bookCopy.id)
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
            var navHostController = navHostFragment.navController
            navHostController = mainNavHostFragments[lastFragmentNum]!!.findNavController()
            navHostController.navigate(R.id.bookDetailsTakenFragment, bundle)
            val view = findViewById<FragmentContainerView>(R.id.fragment_container)
            Snackbar.make(view, "Вы уже взяли эту книгу", Snackbar.LENGTH_SHORT).show()
            return true
        }

        return false
    }
}