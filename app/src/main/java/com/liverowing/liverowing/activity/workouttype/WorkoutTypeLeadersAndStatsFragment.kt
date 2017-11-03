package com.liverowing.liverowing.activity.workouttype


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liverowing.liverowing.R
import com.liverowing.liverowing.adapter.UserStatsAdapter
import com.liverowing.liverowing.model.parse.User
import com.liverowing.liverowing.model.parse.UserStats
import com.liverowing.liverowing.model.parse.WorkoutType
import com.parse.FunctionCallback
import com.parse.ParseCloud
import com.parse.ParseUser
import kotlinx.android.synthetic.main.fragment_workout_type_leaders_and_stats.*

private const val ARGUMENT_WORKOUT_TYPE = "workout_type"

class WorkoutTypeLeadersAndStatsFragment : Fragment() {
    private val userStats = mutableListOf<UserStats>()
    private lateinit var workoutType: WorkoutType

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workout_type_leaders_and_stats, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        workoutType = arguments.getParcelable(ARGUMENT_WORKOUT_TYPE)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        f_workout_type_leaders_and_stats_recyclerview.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = UserStatsAdapter(userStats, { image, userStats ->
                run {
                    Log.d("LiveRowing", "Clicked UserStats row")
                }
            })
            //addItemDecoration(SimpleItemDecorator(15))
        }

        val haveLeaderBoards = true
        if (haveLeaderBoards) {
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
                            f_workout_type_leaders_and_stats_recyclerview.adapter.notifyDataSetChanged()
                        } else {
                            Log.d("LiveRowing", e.message)
                            // TODO: Notify of error
                        }
                    }
            )
        }
    }

    companion object {
        fun newInstance(workoutType: WorkoutType): WorkoutTypeLeadersAndStatsFragment {
            return WorkoutTypeLeadersAndStatsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARGUMENT_WORKOUT_TYPE, workoutType)
                }
            }
        }
    }
}
