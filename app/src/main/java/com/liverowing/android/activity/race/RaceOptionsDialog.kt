package com.liverowing.android.activity.race

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liverowing.android.R
import com.liverowing.android.model.parse.Workout
import com.liverowing.android.service.messages.WorkoutSetup
import kotlinx.android.synthetic.main.fragment_race_options.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by henrikmalmberg on 22/03/2018.
 */
class RaceOptionsDialog : BottomSheetDialogFragment() {
    private lateinit var mWorkoutSetup: WorkoutSetup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_race_options, container, false)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onWorkoutSetup(setup: WorkoutSetup) {
        mWorkoutSetup = setup
        Log.d("LiveRowing", "onWorkoutSetup (dialog)")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        f_race_options_cancel.setOnClickListener {
            dismiss()
        }

        f_race_options_apply.setOnClickListener {
            dismiss()
            mWorkoutSetup.opponent = Workout.fetchWorkout("gzE1xVhxEy")
            EventBus.getDefault().unregister(this)
            EventBus.getDefault().postSticky(mWorkoutSetup)
        }
    }
}
