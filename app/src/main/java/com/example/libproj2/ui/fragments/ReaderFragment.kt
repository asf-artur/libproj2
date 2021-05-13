package com.example.libproj2.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.libproj2.GlobalViewModel
import com.example.libproj2.R
import com.example.libproj2.models.User
import com.example.libproj2.services.BookNetWebAppService
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.integration.android.IntentIntegrator
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable


class ReaderFragment : BaseMainActivityFragment(R.layout.fragment_reader) {
    val globalViewModel : GlobalViewModel by activityViewModels()
    val bookNetWebAppService = BookNetWebAppService.get()
    val usersSearchLiveData : MutableLiveData<List<User>> = MutableLiveData()

    override var toolbarTitle: String? = "Выбор читателя"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        globalViewModel.selectedReaderUser = null

        val barcode_scan_button = view.findViewById<Button>(R.id.barcode_scan_button)
        barcode_scan_button.setOnClickListener {
            scanBarcode()
        }

        val searchview = view.findViewById<SearchView>(R.id.searchview)
        val recyclerview = view.findViewById<RecyclerView>(R.id.recyclerview)

        recyclerview.layoutManager = LinearLayoutManager(requireContext())

        val find_reader_button = view.findViewById<ToggleButton>(R.id.find_reader_button)
        find_reader_button.textOn = "Найти читателя"
        find_reader_button.textOff = "Найти читателя"
        find_reader_button.setOnCheckedChangeListener { buttonView, isChecked ->
            val visibility = if(isChecked) View.VISIBLE else View.GONE
            searchview.visibility = visibility
            recyclerview.visibility = visibility
        }

        usersSearchLiveData.observe(
                viewLifecycleOwner,
                {
                    recyclerview.adapter = ReaderAdapter(it, ::onClick)
                }
        )

        val ed = getEditText(requireContext(), searchview)
        ed?.setOnEditorActionListener { v, actionId, event ->
            if((actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.TYPE_CLASS_PHONE)){
                val observer = object : Observer<List<User>> {
                    override fun onSubscribe(d: Disposable?) {
//                            TODO("Not yet implemented")
                    }

                    override fun onNext(t: List<User>?) {
//                            TODO("Not yet implemented")
                        usersSearchLiveData.value = t
                    }

                    override fun onError(e: Throwable?) {
//                            TODO("Not yet implemented")
                    }

                    override fun onComplete() {
//                            TODO("Not yet implemented")
                    }

                }

                val req = bookNetWebAppService.findUser(v.text.toString())
                req
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(observer)
                true
            }
            false
        }

        searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val observer = object : Observer<List<User>> {
                    override fun onSubscribe(d: Disposable?) {
//                            TODO("Not yet implemented")
                    }

                    override fun onNext(t: List<User>?) {
//                            TODO("Not yet implemented")
                        usersSearchLiveData.value = t
                    }

                    override fun onError(e: Throwable?) {
//                            TODO("Not yet implemented")
                    }

                    override fun onComplete() {
//                            TODO("Not yet implemented")
                    }

                }

                val req = bookNetWebAppService.findUser(query ?: "")
                req
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(observer)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    fun onClick(user: User){
        globalViewModel.selectedReaderUser = user
        findNavController().navigate(R.id.action_readerFragment_to_readerFoundFragment)
    }

    fun scanBarcode(){
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
        integrator.setCameraId(0) // Use a specific camera of the device
        integrator.setOrientationLocked(false)
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }

    fun findUserByBarcode(barcode: String){
        val observer = object : SingleObserver<User>{
            override fun onSubscribe(d: Disposable?) {
//                TODO("Not yet implemented")
            }

            override fun onSuccess(t: User?) {
//                TODO("Not yet implemented")
                globalViewModel.selectedReaderUser = t
                findNavController().navigate(R.id.action_readerFragment_to_readerFoundFragment)
            }

            override fun onError(e: Throwable?) {
//                TODO("Not yet implemented")
                Snackbar.make(requireView(), "Пользователь с таким номером билета не найден", Snackbar.LENGTH_SHORT).show()
            }
        }

        val request = bookNetWebAppService.findUserByBarcode(barcode)
        request
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(data!= null){
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if(result != null){
                Log.d("MYTAG", result.contents)
                findUserByBarcode(result.contents)
            }
        }
    }

    private fun getEditText(context: Context, v: View): EditText? {
        try {
            if (v is ViewGroup) {
                val vg = v
                for (i in 0 until vg.childCount) {
                    val child = vg.getChildAt(i)
                    val editText: View? = getEditText(context, child)
                    if (editText is EditText) {
                        return editText
                    }
                }
            } else if (v is EditText) {
                Log.d("MYTAG", "found edit text")
                return v
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return null
    }
}

private class ReaderAdapter(val users: List<User>, val onClick: (user: User) -> Unit) : RecyclerView.Adapter<ReaderAdapter.ReaderHolder>(){
    inner class ReaderHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(user: User){
            itemView.setOnClickListener {
                onClick(user)
            }
            val imageView = itemView.findViewById<ImageView>(R.id.imageView)
            imageView.setImageResource(R.drawable.profile_no_image)
            val title_textview = itemView.findViewById<TextView>(R.id.title_textview)
            title_textview.text = user.name
            val return_date_textview = itemView.findViewById<TextView>(R.id.return_date_textview)
            return_date_textview.text = user.barcode
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReaderHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_books_on_hand, parent, false)
        return ReaderHolder(view)
    }

    override fun onBindViewHolder(holder: ReaderHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size
}