package com.liverowing.android.activity.workouttype


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.liverowing.android.R
import com.liverowing.android.adapter.UserStatsAdapter
import com.liverowing.android.model.parse.User
import com.liverowing.android.model.parse.UserStats
import com.liverowing.android.model.parse.WorkoutType
import com.parse.FunctionCallback
import com.parse.ParseCloud
import com.parse.ParseUser
import kotlinx.android.synthetic.main.fragment_workout_type_leaders_and_stats.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class WorkoutTypeLeadersAndStatsFragment : Fragment() {
    private val userStats = mutableListOf<UserStats>()
    private lateinit var workoutType: WorkoutType

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workout_type_leaders_and_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        f_workout_type_leaders_and_stats_recyclerview.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = UserStatsAdapter(userStats, Glide.with(this), { image, userStats ->
                run {
                    Log.d("LiveRowing", "Clicked UserStats row")
                }
            })
            //addItemDecoration(SimpleItemDecorator(15))
        }
    }

    @Subscribe(sticky = true)
    fun onWorkoutType(workoutType: WorkoutType) {
        if (workoutType.hasLeaderboards) {
            ParseCloud.callFunctionInBackground(
                    "query_userStats",
                    mapOf(
                            "userClass" to (ParseUser.getCurrentUser() as User).userClass,
                            "record" to "affiliateAndFeaturedWorkouts.WorkoutType$" + workoutType.objectId
                    ),
                    FunctionCallback<List<UserStats>> { stats, e ->
                        if (e == null) {
                            userStats.clear()
                            userStats.addAll(stats)
                            f_workout_type_leaders_and_stats_recyclerview?.adapter?.notifyDataSetChanged()
                        } else {
                            Log.d("LiveRowing", e.message)
                            // TODO: Notify of error
                        }
                    }
            )
        } else {
            Log.d("LiveRowing", "No leaderboards for this workout :/")
        }

    }
}
