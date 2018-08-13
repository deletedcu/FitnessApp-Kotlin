package com.liverowing.android.workoutshared.details


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.liverowing.android.LiveRowing
import com.liverowing.android.R
import com.liverowing.android.model.parse.WorkoutType
import org.greenrobot.eventbus.EventBus

class WorkoutHistoryFragment : Fragment() {
    override fun onDestroy() {
        super.onDestroy()
        LiveRowing.refWatcher(this.activity).watch(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workout_browser_detail_workout_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val workoutType = EventBus.getDefault().getStickyEvent(WorkoutType::class.java)

    }
}
