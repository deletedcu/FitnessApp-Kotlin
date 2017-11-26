package com.liverowing.liverowing.activity.race

import android.content.Intent
import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.liverowing.liverowing.LiveRowing.Companion.BluetoothDeviceConnected
import com.liverowing.liverowing.R
import com.liverowing.liverowing.activity.devicescan.DeviceScanActivity
import com.liverowing.liverowing.secondsToTimespan
import com.liverowing.liverowing.model.pm.AdditionalRowingStatus1
import com.liverowing.liverowing.model.pm.RowingStatus
import com.liverowing.liverowing.model.parse.WorkoutType
import com.liverowing.liverowing.service.messages.ProgramWorkout
import kotlinx.android.synthetic.main.activity_race.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class RaceActivity : AppCompatActivity() {
    private lateinit var mWorkoutType: WorkoutType
    private var mWorkoutProgrammed = false

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_race)

        EventBus.getDefault().register(this)
    }

    override fun onStart() {
        super.onStart()

        if (!BluetoothDeviceConnected) {
            startActivity(Intent(this@RaceActivity, DeviceScanActivity::class.java))
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            if (!mWorkoutProgrammed) {
                EventBus.getDefault().post(ProgramWorkout(mWorkoutType))
            }
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onWorkoutType(workoutType: WorkoutType) {
        mWorkoutType = workoutType

        val sb = StringBuilder()
        sb.append("WorkoutType: " + mWorkoutType.name + "\n")
        sb.append("ValueType: " + mWorkoutType.valueType + "\n")
        sb.append("Value: " + mWorkoutType.value + "\n")

        a_race_debug.text = sb.toString()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRowingStatus(data: RowingStatus) {
        a_race_status.text = data.workoutState.toString()
        a_race_time.text = data.elapsedTime.secondsToTimespan(true)
        a_race_distance.text = data.distance.toString() + "m"
        a_race_stroke_state.text = data.strokeState.toString()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAdditionalRowingStatus1(data: AdditionalRowingStatus1) {
        a_race_spm.text = data.strokeRate.toString()
    }
}
