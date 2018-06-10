package com.liverowing.android.activity.dashboard

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liverowing.android.R
import com.liverowing.android.adapter.DashboardWorkoutTypeAdapter
import com.liverowing.android.model.parse.WorkoutType
import com.liverowing.android.screenWidth
import com.liverowing.android.util.SimpleItemDecorator
import kotlinx.android.synthetic.main.fragment_dashboard.*
import android.support.v4.app.ActivityOptionsCompat
import com.bumptech.glide.Glide
import com.liverowing.android.LiveRowing
import com.liverowing.android.activity.workouttype.*
import com.liverowing.android.model.parse.Workout
import org.greenrobot.eventbus.EventBus


class DashboardFragment : Fragment() {
    private val featuredWorkoutsList = arrayListOf<WorkoutType>()
    private val recentAndLikedWorkoutsList = arrayListOf<WorkoutType>()
    private val myCustomWorkoutsList = arrayListOf<WorkoutType>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFeaturedWorkouts()
        setupRecentAndLikedWorkouts()
        setupMyCustomWorkouts()

        f_dashboard_featured_title.setOnClickListener { this.onClickedWorkoutTypeHeader(it) }
        f_dashboard_liked_and_recent_title.setOnClickListener { this.onClickedWorkoutTypeHeader(it) }
        f_dashboard_mycustom_title.setOnClickListener { this.onClickedWorkoutTypeHeader(it) }
    }

    private fun setupFeaturedWorkouts() {
        val cardWidth = 600
        f_dashboard_featured_recyclerview.apply {
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
            adapter = DashboardWorkoutTypeAdapter(featuredWorkoutsList, Glide.with(this), cardWidth, null, { image, workoutType ->
                run {
                    EventBus.getDefault().postSticky(workoutType)
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, image, "image")
                    activity!!.startActivity(Intent(activity, WorkoutTypeDetailActivity::class.java), options.toBundle())
                }
            })
            isHorizontalScrollBarEnabled = false
            addItemDecoration(SimpleItemDecorator(15))
        }
        LinearSnapHelper().attachToRecyclerView(f_dashboard_featured_recyclerview)

        val featuredWorkouts = WorkoutType.featuredWorkouts()
        featuredWorkouts.findInBackground { objects, e ->
            if (e === null) {
                featuredWorkoutsList.clear()
                featuredWorkoutsList.addAll(objects)
                f_dashboard_featured_recyclerview?.adapter?.notifyDataSetChanged()
            } else {
                LiveRowing.globalParseExceptionHandler(activity!!, e)
            }
        }
    }

    private fun setupMyCustomWorkouts() {
        val cardWidth = 400
        f_dashboard_mycustom_recyclerview.apply {
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
            adapter = DashboardWorkoutTypeAdapter(myCustomWorkoutsList, Glide.with(this), cardWidth, null, { image, workoutType ->
                run {
                    EventBus.getDefault().postSticky(workoutType)
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, image, "image")
                    activity!!.startActivity(Intent(activity, WorkoutTypeDetailActivity::class.java), options.toBundle())
                }
            })
            isHorizontalScrollBarEnabled = false
            addItemDecoration(SimpleItemDecorator(15))
        }
        LinearSnapHelper().attachToRecyclerView(f_dashboard_mycustom_recyclerview)

        val myCustomWorkouts = WorkoutType.myCustomWorkouts()
        myCustomWorkouts.findInBackground { objects, e ->
            if (e === null) {
                myCustomWorkoutsList.clear()
                myCustomWorkoutsList.addAll(objects)
                f_dashboard_mycustom_recyclerview?.adapter?.notifyDataSetChanged()
            } else {
                LiveRowing.globalParseExceptionHandler(activity!!, e)
            }
        }
    }


    private fun setupRecentAndLikedWorkouts() {
        val cardWidth = minOf(400, (activity!!.screenWidth() * .45).toInt())
        f_dashboard_liked_and_recent_recyclerview.apply {
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
            adapter = DashboardWorkoutTypeAdapter(recentAndLikedWorkoutsList, Glide.with(this), cardWidth, null, { image, workoutType ->
                run {
                    EventBus.getDefault().postSticky(workoutType)
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, image, "image")
                    activity!!.startActivity(Intent(activity, WorkoutTypeDetailActivity::class.java), options.toBundle())
                }
            })
            isHorizontalScrollBarEnabled = false
            addItemDecoration(SimpleItemDecorator(15))
        }
        LinearSnapHelper().attachToRecyclerView(f_dashboard_liked_and_recent_recyclerview)

        val recentAndLikedWorkouts = WorkoutType.recentAndLikedWorkouts()
        recentAndLikedWorkouts.findInBackground { objects, e ->
            if (e === null) {
                recentAndLikedWorkoutsList.clear()
                recentAndLikedWorkoutsList.addAll(objects)
                f_dashboard_liked_and_recent_recyclerview?.adapter?.notifyDataSetChanged()
            } else {
                LiveRowing.globalParseExceptionHandler(activity!!, e)
            }
        }
    }

    private fun onClickedWorkoutTypeHeader(sender: View) {
        val workoutCategory = when (sender.id) {
            R.id.f_dashboard_featured_title -> WORKOUT_CATEGORY_FEATURED
            R.id.f_dashboard_liked_and_recent_title -> WORKOUT_CATEGORY_RECENT_AND_LIKED
            R.id.f_dashboard_mycustom_title -> WORKOUT_CATEGORY_CUSTOM
            else -> WORKOUT_CATEGORY_FEATURED
        }
        startActivity(activity!!.WorkoutTypeGridIntent(workoutCategory))
    }
}
