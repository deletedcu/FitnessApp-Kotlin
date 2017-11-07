package com.liverowing.liverowing.activity.race

import android.content.*
import android.content.pm.ActivityInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.liverowing.liverowing.R
import com.liverowing.liverowing.csafe.Communication
import com.liverowing.liverowing.secondsToTimespan
import com.liverowing.liverowing.model.pm.AdditionalRowingStatus1
import com.liverowing.liverowing.model.pm.RowingStatus
import com.liverowing.liverowing.model.pm.StrokeData
import com.liverowing.liverowing.model.pm.WorkoutType
import com.liverowing.liverowing.service.PerformanceMonitorBLEService
import kotlinx.android.synthetic.main.activity_race.*

fun Context.RaceIntent(workoutType: WorkoutType?): Intent {
    return Intent(this, RaceActivity::class.java).apply {
        putExtra(INTENT_WORKOUT_TYPE, workoutType)
    }
}

private const val INTENT_WORKOUT_TYPE = "workout_type"

class RaceActivity : AppCompatActivity() {
    private var mServiceBound = false
    private lateinit var mPerformanceMonitorBLEService: PerformanceMonitorBLEService
    private lateinit var mServiceConnection: ServiceConnection

    private lateinit var mBroadCastReceiver: BroadcastReceiver

    override fun onStart() {
        super.onStart()

        mServiceConnection = object: ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                mServiceBound = true
                mPerformanceMonitorBLEService = (binder as PerformanceMonitorBLEService.Binder).service

                if (!mPerformanceMonitorBLEService.Connected) {
                    a_race_status.text = "Go back and click the bluetooth icon to connect"
                } else {
                    mPerformanceMonitorBLEService.sendCsafeCommand(Communication().justRow())
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                mServiceBound = false
            }
        }

        val intent = Intent(this, PerformanceMonitorBLEService::class.java)
        startService(intent)
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()

        Log.d("LiveRowing", "BroadcastReceiver unregistered")
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadCastReceiver)
        if (mServiceBound) {
            unbindService(mServiceConnection)
            mServiceBound = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_race)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun onResume() {
        super.onResume()

        Log.d("LiveRowing", "BroadcastReceiver set up")
        mBroadCastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.getStringExtra("characteristic")) {
                    RowingStatus::class.java.simpleName -> {
                        val data = intent?.getParcelableExtra<RowingStatus>("data")
                        a_race_status.text = data?.workoutState.toString()
                        a_race_time.text = data?.elapsedTime?.secondsToTimespan(true)
                        a_race_distance.text = data?.distance.toString() + "m"
                        a_race_stroke_state.text = data?.strokeState.toString()
                    }

                    AdditionalRowingStatus1::class.java.simpleName -> {
                        val data = intent?.getParcelableExtra<AdditionalRowingStatus1>("data")
                        a_race_spm.text = data?.strokeRate.toString()
                    }

                    StrokeData::class.java.simpleName -> {
                        val data = intent?.getParcelableExtra<StrokeData>("data")
                    }
                }
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadCastReceiver, IntentFilter(PerformanceMonitorBLEService.BROADCAST_CHARACTERISTICS_CHANGED))
    }
}
