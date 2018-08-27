package com.liverowing.android.workouthistory.detail.summary

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liverowing.android.MainActivity
import com.liverowing.android.R
import com.liverowing.android.model.pm.SplitTitle
import kotlinx.android.synthetic.main.dialog_split.*
import kotlinx.android.synthetic.main.dialog_split.view.*

class SplitDialogFragment(private var selectedList: List<SplitTitle>): DialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: SplitDialogAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val dataSet = SplitTitle.fullData()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        setupUI()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_feets, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.summary_split, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_close -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupUI() {
        dialog.setTitle("Metric Picker")
        viewManager = LinearLayoutManager(activity!!)
        viewAdapter = SplitDialogAdapter(dataSet, selectedList)

        recyclerView = f_workout_dialog_split_recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }
}