package com.liverowing.android.workoutshared.details


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.liverowing.android.LiveRowing
import com.liverowing.android.R
import com.liverowing.android.extensions.toggleVisibility
import com.liverowing.android.model.parse.Segment
import com.liverowing.android.model.parse.WorkoutType
import com.liverowing.android.workoutbrowser.WorkoutBrowserFragment
import com.liverowing.android.workoutbrowser.detail.WorkoutBrowserDetailFragmentDirections
import kotlinx.android.synthetic.main.fragment_workout_details.*
import timber.log.Timber

class WorkoutDetailFragment : Fragment() {
    private lateinit var workoutType: WorkoutType
    companion object {
        fun newInstance(workoutType: WorkoutType) : WorkoutDetailFragment {
            val fragment = WorkoutDetailFragment()
            fragment.workoutType = workoutType
            return fragment
        }
    }

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

        if (workoutType.friendlySegmentDescription.isNotEmpty()) {
            f_workout_detail_intervals.text = workoutType.friendlySegmentDescription
            f_workout_detail_intervals_title.visibility = View.VISIBLE
            f_workout_detail_intervals.visibility = View.VISIBLE
        } else {
            f_workout_detail_intervals_title.visibility = View.GONE
            f_workout_detail_intervals.visibility = View.GONE
        }
        f_workout_detail_description.text = workoutType.descriptionText

        f_workout_detail_more_like_user.text = "More from ${workoutType.createdBy?.username}"
        if (workoutType.filterTags != null && workoutType.filterTags!!.isNotEmpty()) {
            f_workout_detail_more_like_tags.visibility = View.VISIBLE
            f_workout_detail_more_like_tags.text = "More ${workoutType.filterTagsFriendly.joinToString(" & ")} workouts"
            f_workout_detail_more_like_tags.setOnClickListener {
                val action = WorkoutBrowserDetailFragmentDirections.workoutBrowserAction()
                action.setCategory(WorkoutBrowserFragment.CATEGORY_FEATURED)
                action.setTags(workoutType.filterTagsActiveIndexes.joinToString(","))
                findNavController().navigate(action)
            }

            /* TODO: Eh, not sure..
            workoutType.filterTagsFriendly.forEach {
                val chip = Chip(activity)
                chip.text = it
                f_workout_detail_tags.addView(chip)
            }
            */
        } else {
            f_workout_detail_more_like_tags.visibility = View.GONE
        }

        f_workout_detail_intervals_title.setOnClickListener {
            f_workout_detail_segments_recyclerview.toggleVisibility()
        }

        viewManager = LinearLayoutManager(activity!!)
        viewDividerItemDecoration = DividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL)
        viewAdapter = WorkoutSegmentAdapter(dataSet, Glide.with(context!!)) { _, segment ->
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

