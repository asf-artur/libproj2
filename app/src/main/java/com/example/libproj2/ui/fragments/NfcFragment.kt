package com.example.libproj2.ui.fragments

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import com.example.libproj2.GlobalViewModel
import com.example.libproj2.R

class NfcFragment : BaseMainActivityFragment(R.layout.fragment_nfc) {

    val globalViewModel : GlobalViewModel by activityViewModels()

    var mAdapter: NfcAdapter? = null
    lateinit var mPendingIntent: PendingIntent

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nfc = NfcAdapter.getDefaultAdapter(requireContext())
        Log.d("MYTAG", "enabled == ${nfc.isEnabled == true}")

        mAdapter = globalViewModel.mAdapter
        mPendingIntent = globalViewModel.mPendingIntent
    }

    override fun onResume() {
        super.onResume()
        mAdapter?.enableForegroundDispatch(requireActivity(), mPendingIntent, null, null);
    }

    override fun onPause() {
        super.onPause()
        if (mAdapter != null) {
            mAdapter?.disableForegroundDispatch(requireActivity());
        }
    }
}