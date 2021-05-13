package com.example.libproj2

import android.app.PendingIntent
import android.nfc.NfcAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.libproj2.models.BookCopy
import com.example.libproj2.models.SearchSource
import com.example.libproj2.models.SearchSourceType
import com.example.libproj2.models.User

class GlobalViewModel : ViewModel() {
    lateinit var activeUser: User

    var scannedBookCopy: MutableLiveData<BookCopy> = MutableLiveData()

    var searchSources : MutableMap<SearchSourceType, Boolean> = mutableMapOf(
            SearchSourceType.LibraryBookInfo to true,
            SearchSourceType.LibraryBookCopy to false,
            SearchSourceType.Google to false,
    )

    var selectedReaderUser : User? = null

    var mAdapter: NfcAdapter? = null
    lateinit var mPendingIntent: PendingIntent
}