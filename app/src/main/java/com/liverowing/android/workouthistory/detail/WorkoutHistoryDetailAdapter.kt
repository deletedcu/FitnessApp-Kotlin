package com.liverowing.android.workouthistory.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.liverowing.android.workouthistory.detail.summary.WorkoutHistoryDetailChartsFragment
import com.liverowing.android.workouthistory.detail.summary.WorkoutHistoryDetailDetailsFragment
import com.liverowing.android.workouthistory.detail.summary.WorkoutHistoryDetailLeaderboardsFragment
import com.liverowing.android.workouthistory.detail.summary.WorkoutHistoryDetailSplitsFragment

class WorkoutHistoryDetailAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> WorkoutHistoryDetailChartsFragment()
            1 -> WorkoutHistoryDetailSplitsFragment()
            2 -> WorkoutHistoryDetailChartsFragment()
            3 -> WorkoutHistoryDetailLeaderboardsFragment()
            4 -> WorkoutHistoryDetailDetailsFragment()
            else -> Fragment()
        }
    }

    override fun getCount(): Int {
        return 5
    }
}