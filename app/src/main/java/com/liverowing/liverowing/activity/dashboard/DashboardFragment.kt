package com.liverowing.liverowing.activity.dashboard

import android.app.ActivityOptions
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liverowing.liverowing.R
import com.liverowing.liverowing.activity.workouttype.WorkoutTypeDetailIntent
import com.liverowing.liverowing.adapter.DashboardWorkoutTypeAdapter
import com.liverowing.liverowing.api.model.WorkoutType
import com.liverowing.liverowing.screenWidth
import com.liverowing.liverowing.util.SimpleItemDecorator
import kotlinx.android.synthetic.main.fragment_dashboard.*


class DashboardFragment : Fragment() {
    val featuredWorkoutsList = mutableListOf<WorkoutType>()
    val recentAndLikedWorkoutsList = mutableListOf<WorkoutType>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFeaturedWorkouts()
        setupRecentAndLikedWorkouts()
    }

    private fun setupFeaturedWorkouts() {
        val cardWidth = minOf(600, (activity!!.screenWidth() * .75).toInt())
        f_dashboard_featured_recyclerview.apply {
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
            adapter = DashboardWorkoutTypeAdapter(featuredWorkoutsList, cardWidth, { image, workoutType ->
                run {
                    val options = ActivityOptions.makeSceneTransitionAnimation(activity, image, "image")
                    activity.startActivity(activity.WorkoutTypeDetailIntent(workoutType), options.toBundle())
                }
            })
            isHorizontalScrollBarEnabled = false
            addItemDecoration(SimpleItemDecorator(5, true))
        }
        LinearSnapHelper().attachToRecyclerView(f_dashboard_featured_recyclerview)

        val featuredWorkouts = WorkoutType.featuredWorkouts()
        featuredWorkouts.findInBackground { objects, e ->
            if (e === null) {
                featuredWorkoutsList.clear()
                featuredWorkoutsList.addAll(objects)
                f_dashboard_featured_recyclerview.adapter.notifyDataSetChanged()
            } else {
                Log.d("LiveRowing", e.message)
            }
        }
    }

    private fun setupRecentAndLikedWorkouts() {
        val cardWidth = minOf(400, (activity!!.screenWidth() * .45).toInt())
        f_dashboard_liked_and_recent_recyclerview.apply {
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
            adapter = DashboardWorkoutTypeAdapter(recentAndLikedWorkoutsList, cardWidth, { image, workoutType ->
                run {
                    val options = ActivityOptions.makeSceneTransitionAnimation(activity, image, "image")
                    activity.startActivity(activity.WorkoutTypeDetailIntent(workoutType), options.toBundle())
                }
            })
            isHorizontalScrollBarEnabled = false
            addItemDecoration(SimpleItemDecorator(5, true))
        }
        LinearSnapHelper().attachToRecyclerView(f_dashboard_liked_and_recent_recyclerview)

        val recentAndLikedWorkouts = WorkoutType.recentAndLikedWorkouts()
        recentAndLikedWorkouts.findInBackground { objects, e ->
            if (e === null) {
                recentAndLikedWorkoutsList.clear()
                recentAndLikedWorkoutsList.addAll(objects)
                f_dashboard_liked_and_recent_recyclerview.adapter.notifyDataSetChanged()
            } else {
                Log.d("LiveRowing", e.message)
            }
        }
    }

    companion object {
        fun newInstance(): DashboardFragment {
            return DashboardFragment()
        }
    }
}
