package com.example.libproj2.utils

import androidx.appcompat.app.AppCompatActivity

abstract class TaggedActivity : AppCompatActivity() {
    val TAG = this::class.java.simpleName
}