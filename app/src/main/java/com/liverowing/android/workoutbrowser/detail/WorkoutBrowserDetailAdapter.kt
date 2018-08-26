package com.liverowing.android.workoutbrowser.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.liverowing.android.model.parse.WorkoutType
import com.liverowing.android.workoutshared.details.WorkoutDetailFragment
import com.liverowing.android.workoutshared.history.WorkoutHistoryFragment
import com.liverowing.android.workoutshared.leaderboards.WorkoutLeaderBoardsFragment

class WorkoutBrowserDetailAdapter(fm: FragmentManager, val workoutType: WorkoutType) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> WorkoutDetailFragment.newInstance(workoutType)
            1 -> WorkoutLeaderBoardsFragment.newInstance(workoutType)
            3 -> WorkoutHistoryFragment.newInstance(workoutType)
            else -> Fragment()
        }
    }

    override fun getCount(): Int {
        return 3
    }
}