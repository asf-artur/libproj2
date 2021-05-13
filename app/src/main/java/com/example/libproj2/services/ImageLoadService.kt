package com.example.libproj2.services

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.File
import java.io.FileInputStream

class ImageLoadService {
    fun loadImage(path: String, context: Context): Drawable? {
        val externalCacheFile = File(context.externalCacheDir, path)
        val result = Drawable.createFromPath(externalCacheFile.path)

        return result
    }

    fun setBarcodeImage(barcode: String, imageView: ImageView){
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.encodeBitmap(barcode, BarcodeFormat.CODE_128, 600,400)
        imageView.setImageBitmap(bitmap)
    }
}