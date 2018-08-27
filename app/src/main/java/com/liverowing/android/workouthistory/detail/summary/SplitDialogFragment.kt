package com.liverowing.android.workouthistory.detail.summary

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liverowing.android.R
import com.liverowing.android.model.pm.SplitTitle
import kotlinx.android.synthetic.main.dialog_split.view.*

class SplitDialogFragment(private var selectedList: MutableList<SplitTitle>, private val onSelectedItems: (selectedList: MutableList<SplitTitle>) -> Unit): DialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: SplitDialogAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val dataSet = SplitTitle.fullData()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_split, container, false)
        setupUI(view)
        return view
    }

    private fun setupUI(v: View) {
        viewManager = LinearLayoutManager(activity!!)
        viewAdapter = SplitDialogAdapter(dataSet, selectedList, onCheckChanged = {state, selectedItems ->
            if (!state) {
                Toast.makeText(context!!, "Sorry, only four metric at time cowboy", Toast.LENGTH_LONG).show()
            }
            selectedList = selectedItems
        })

        recyclerView = v.f_workout_dialog_split_recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val visibleItemCount = layoutManager!!.childCount
                    val totalItemCount = layoutManager!!.itemCount
                    val firstVisibleItemPosition = (viewManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
//                        onDismiss()
                    }
                }
            })
        }

        v.dialog_split_close.setOnClickListener {
            onDismiss()
        }
    }

    private fun onDismiss() {
        if (selectedList.size < 4) {
            Toast.makeText(context!!, "User should choose only 4 metrics at a time", Toast.LENGTH_LONG).show()
        } else {
            onSelectedItems(selectedList)
            dismiss()
        }
    }
}