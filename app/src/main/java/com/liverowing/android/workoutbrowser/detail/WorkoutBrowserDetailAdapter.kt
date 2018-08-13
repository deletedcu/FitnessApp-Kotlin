package com.liverowing.android.workoutbrowser.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.liverowing.android.workoutshared.details.WorkoutDetailFragment
import com.liverowing.android.workoutshared.details.WorkoutHistoryFragment
import com.liverowing.android.workoutshared.leaderboards.WorkoutLeaderBoardsFragment

class WorkoutBrowserDetailAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> WorkoutDetailFragment()
            1 -> WorkoutLeaderBoardsFragment()
            3 -> WorkoutHistoryFragment()
            else -> Fragment()
        }
    }

    override fun getCount(): Int {
        return 3
    }
}