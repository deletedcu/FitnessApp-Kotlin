package com.liverowing.android.workoutbrowser.detail

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.liverowing.android.workoutbrowser.detail.leadersandstats.WorkoutBrowserDetailLeadersAndStatsFragment
import com.liverowing.android.workoutshared.workoutdetails.WorkoutBrowserDetail
import com.liverowing.android.workoutshared.workoutdetails.WorkoutBrowserDetailWorkoutHistoryFragment

class WorkoutBrowserDetailAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> WorkoutBrowserDetail()
            1 -> WorkoutBrowserDetailLeadersAndStatsFragment()
            3 -> WorkoutBrowserDetailWorkoutHistoryFragment()
            else -> Fragment()
        }
    }

    override fun getCount(): Int {
        return 3
    }
}