package com.example.libproj2.utils

import androidx.fragment.app.Fragment

abstract class TaggedFragment : Fragment() {
    val TAG = this::class.java.simpleName
}