package com.liverowing.android.workouthistory.detail.summary


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.liverowing.android.LiveRowing
import com.liverowing.android.R
import org.greenrobot.eventbus.EventBus

class WorkoutHistorySummaryFragment : Fragment() {

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this@WorkoutHistorySummaryFragment)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this@WorkoutHistorySummaryFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        LiveRowing.refWatcher(this.activity).watch(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workout_history_detail_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}
