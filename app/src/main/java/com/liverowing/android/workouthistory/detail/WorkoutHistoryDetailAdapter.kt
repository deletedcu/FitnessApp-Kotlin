package com.liverowing.android.workouthistory.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.liverowing.android.workouthistory.detail.summary.WorkoutHistoryChartsFragment
import com.liverowing.android.workouthistory.detail.summary.WorkoutHistoryDetailsFragment
import com.liverowing.android.workouthistory.detail.summary.WorkoutHistorySplitsFragment
import com.liverowing.android.workoutshared.leaderboards.WorkoutLeaderBoardsFragment

class WorkoutHistoryDetailAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> WorkoutHistoryChartsFragment()
            1 -> WorkoutHistorySplitsFragment()
            2 -> WorkoutHistoryChartsFragment()
            3 -> WorkoutLeaderBoardsFragment()
            4 -> WorkoutHistoryDetailsFragment()
            else -> Fragment()
        }
    }

    override fun getCount(): Int {
        return 5
    }
}