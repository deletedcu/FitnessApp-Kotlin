package com.liverowing.liverowing.activity.race

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64.DEFAULT
import android.util.Base64.decode
import android.util.JsonReader
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.liverowing.liverowing.R
import com.liverowing.liverowing.model.parse.Workout
import com.liverowing.liverowing.model.parse.WorkoutType
import com.liverowing.liverowing.service.messages.WorkoutSetup
import kotlinx.android.synthetic.main.activity_race.*
import kotlinx.android.synthetic.main.activity_setup_race.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

class SetupRaceActivity : AppCompatActivity() {
    private lateinit var mWorkoutType: WorkoutType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_race)

        setSupportActionBar(a_setup_race_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        EventBus.getDefault().register(this)

        a_setup_race_start.setOnClickListener {
            // TODO: send real values for opponent and target pace
            // TODO: This is only here for testing!
            val opponent = Workout.fetchWorkout("f91xAlF4PW")
            opponent.loadForPlayback()

            EventBus.getDefault().postSticky(WorkoutSetup(mWorkoutType, opponent, null, 82))
            startActivity(Intent(this@SetupRaceActivity, RaceActivity::class.java))
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onWorkoutType(workoutType: WorkoutType) {
        mWorkoutType = workoutType

        if (!mWorkoutType.isDataAvailable) {
            // TODO: Should be done in background thread and use a loader?
            mWorkoutType = WorkoutType.fetchWorkout(mWorkoutType.objectId)
            Log.d("LiveRowing", mWorkoutType.createdBy?.username)
        }

        supportActionBar?.title = mWorkoutType.name
        if (mWorkoutType.image?.url != null) {
            Glide.with(this).load(mWorkoutType.image?.url).into(a_setup_race_image)
        }
    }
}
