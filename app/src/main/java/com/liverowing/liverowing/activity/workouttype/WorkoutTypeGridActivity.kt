package com.liverowing.liverowing.activity.workouttype

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.liverowing.liverowing.R
import com.liverowing.liverowing.api.model.WorkoutType
import com.parse.ParseQuery

fun Context.WorkoutTypeGridIntent(query: ParseQuery<WorkoutType>): Intent {
    return Intent(this, WorkoutTypeGridActivity::class.java).apply {

    }
}

private const val INTENT_WORKOUT_TYPES = "workout_types"

class WorkoutTypeGridActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_type_grid)
    }
}
