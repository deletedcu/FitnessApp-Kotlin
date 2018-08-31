package com.liverowing.android.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.liverowing.android.R
import com.liverowing.android.model.parse.User
import kotlinx.android.synthetic.main.dialog_choose_featured.view.*

class FeaturedChooseDialogFragment(private var featuredList: MutableList<User>, private var choosedList: MutableList<User>, private val onApplyClick: (MutableList<User>) -> Unit): DialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: FeaturedChooseDialogAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var selectedList = mutableListOf<User>()

    init {
        selectedList.addAll(choosedList)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_choose_featured, container, false)
        setupUI(view)
        return view
    }

    private fun setupUI(view: View) {
        viewManager = LinearLayoutManager(activity!!)
        viewAdapter = FeaturedChooseDialogAdapter(featuredList, selectedList, Glide.with(activity!!), onUpdatedItems = {
            selectedList.clear()
            selectedList.addAll(it)
        })

        recyclerView = view.dialog_choose_featured_recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        view.dialog_choose_featured_apply.setOnClickListener {
            onApplyClick(selectedList)
            dismiss()
        }

        view.dialog_choose_featured_cancel.setOnClickListener {
            dismiss()
        }
    }
}