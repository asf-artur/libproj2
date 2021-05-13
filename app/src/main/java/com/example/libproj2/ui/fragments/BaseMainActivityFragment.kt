package com.example.libproj2.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.libproj2.MainApp
import com.example.libproj2.R
import com.example.libproj2.utils.ToolbarOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar

abstract class BaseMainActivityFragment(val contentLayoutId: Int):
        Fragment() {
    private lateinit var _activityCallback: ActivityCallback
    private lateinit var _topAppBar: MaterialToolbar

    lateinit var appBarLayout: AppBarLayout

    val topAppBar: MaterialToolbar
    get() { return _topAppBar }

    open val toolbarOptions: ToolbarOptions? = null

    open var toolbarTitle: String? = null

    interface ActivityCallback{
        fun goToFragmentCallback(fragment: Fragment, addToBackStack: Boolean = true)
    }


    open fun setupTopAppBar(){
        topAppBar.visibility = View.VISIBLE
        _topAppBar.menu.clear()

        toolbarTitle = "fffff"
        if(toolbarTitle == null){
            toolbarTitle = if(parentFragment != null) (parentFragment as BaseMainActivityFragment).toolbarTitle
            else "Библиотека"
        }
        _topAppBar.title = toolbarTitle
        toolbarOptions?.let {
            _topAppBar.inflateMenu(it.menuResId)
            _topAppBar.setOnMenuItemClickListener(it.menuItemClickListener)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        MainApp.component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(contentLayoutId, container, false) as ViewGroup
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _activityCallback = requireActivity() as ActivityCallback
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val topBar = view.findViewById<MaterialToolbar>(R.id.toolbar_default )
        if(topBar == null){
            Log.d("MYTAG", "--null")
        }
        else{
            Log.d("MYTAG", "--top")
            toolbarTitle?.let {
                topBar.title = toolbarTitle
            }
            toolbarOptions?.let{
                topBar.inflateMenu(it.menuResId)
                topBar.setOnMenuItemClickListener(it.menuItemClickListener)
            }
        }
//        setupTopAppBar()
    }

    fun goToFragment(fragment: Fragment, addToBackStack: Boolean = true){
        _activityCallback.goToFragmentCallback(fragment, addToBackStack)
    }
}