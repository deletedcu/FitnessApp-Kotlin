package com.liverowing.android.workoutshared.details


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.liverowing.android.LiveRowing
import com.liverowing.android.R
import com.liverowing.android.model.parse.Segment
import com.liverowing.android.model.parse.WorkoutType
import kotlinx.android.synthetic.main.fragment_workout_details.*
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class WorkoutDetailFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewDividerItemDecoration: DividerItemDecoration

    private var dataSet = mutableListOf<Segment>()

    override fun onDestroy() {
        super.onDestroy()
        LiveRowing.refWatcher(this.activity).watch(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workout_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val workoutType = EventBus.getDefault().getStickyEvent(WorkoutType::class.java)

        f_workout_detail_intervals.text = workoutType.friendlySegmentDescription
        f_workout_detail_description.text = workoutType.descriptionText

        viewManager = LinearLayoutManager(activity!!)
        viewDividerItemDecoration = DividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL)
        viewAdapter = WorkoutSegmentAdapter(dataSet, Glide.with(this)) { _, segment ->
            Timber.d("Clicked a segment row: %s", segment)
        }

        recyclerView = f_workout_detail_segments_recyclerview.apply {
            addItemDecoration(viewDividerItemDecoration)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        dataSet.clear()
        if (workoutType.segments is List<Segment>) {
            dataSet.addAll(workoutType.segments!!)
            viewAdapter.notifyDataSetChanged()
        }
    }
}