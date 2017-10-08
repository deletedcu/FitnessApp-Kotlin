package com.liverowing.liverowing.activity.dashboard

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import com.liverowing.liverowing.R
import com.liverowing.liverowing.adapter.DashboardWorkoutTypeAdapter
import com.liverowing.liverowing.api.model.User
import com.liverowing.liverowing.api.model.WorkoutType
import com.liverowing.liverowing.util.SimpleItemDecorator
import com.parse.ParseQuery
import kotlinx.android.synthetic.main.fragment_dashboard.*


class DashboardFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val featuredUsers = ParseQuery.getQuery(User::class.java)
        featuredUsers.whereEqualTo("isFeatured", true)

        val featuredWorkouts = ParseQuery.getQuery(WorkoutType::class.java)
        featuredWorkouts.include("createdBy")
        featuredWorkouts.whereEqualTo("isFeatured", true)
        featuredWorkouts.whereMatchesQuery("createdBy", featuredUsers)
        featuredWorkouts.addAscendingOrder("createdBy")
        featuredWorkouts.addDescendingOrder("createdAt")

        featuredWorkouts.findInBackground { objects, e ->
            if (e === null) {
                f_dashboard_featured_recyclerview.apply {
                    layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
                    adapter = DashboardWorkoutTypeAdapter(objects, { _ -> Log.d("LiveRowing", "listener!") })
                    isHorizontalScrollBarEnabled = false
                    addItemDecoration(SimpleItemDecorator(5, true))
                }

                val snapHelper = LinearSnapHelper()
                snapHelper.attachToRecyclerView(f_dashboard_featured_recyclerview)
            } else {
                Log.d("LiveRowing", e.message)
            }
        }

    }

    companion object {
        fun newInstance(): DashboardFragment {
            Log.d("LiveRowing", "newInstance")
            return DashboardFragment()
        }
    }

}
