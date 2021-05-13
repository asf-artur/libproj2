package com.example.libproj2.ui.fragments

import android.R.attr
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.libproj2.R
import com.google.zxing.integration.android.IntentIntegrator


private const val REQUEST_CODE = 0
class ScanBarcodeFragment : BaseMainActivityFragment(R.layout.fragment_scan_barcode) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
        integrator.setCameraId(0) // Use a specific camera of the device
        integrator.setOrientationLocked(false)
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if(result != null){
            Log.d("MYTAG", result.contents)
        }
    }
}