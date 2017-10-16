package com.liverowing.liverowing.activity.workouttype

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.liverowing.liverowing.R
import com.liverowing.liverowing.api.model.WorkoutType
import com.liverowing.liverowing.loadUrl
import kotlinx.android.synthetic.main.activity_workout_type_detail.*

fun Context.WorkoutTypeDetailIntent(workoutType: WorkoutType): Intent {
    return Intent(this, WorkoutTypeDetailActivity::class.java).apply {
        putExtra(INTENT_WORKOUT_TYPE_ID, workoutType.objectId)
        putExtra(INTENT_WORKOUT_IMAGE_URL, workoutType.image?.url)
    }
}

private const val INTENT_WORKOUT_TYPE_ID = "workout_type_id"
private const val INTENT_WORKOUT_IMAGE_URL = "workout_type_image_url"

class WorkoutTypeDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_type_detail)

        Log.d("LiveRowing", "LOADED!")
        val bundle = intent.extras
        if (bundle.getString(INTENT_WORKOUT_IMAGE_URL) != null) {
            Log.d("LiveRowing", "GOT URL!!")
            a_workout_type_detail_image.loadUrl(bundle.getString(INTENT_WORKOUT_IMAGE_URL))
        }
    }
}
