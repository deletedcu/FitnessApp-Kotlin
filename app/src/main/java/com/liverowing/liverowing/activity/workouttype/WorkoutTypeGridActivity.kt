package com.liverowing.liverowing.activity.workouttype

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.liverowing.liverowing.R
import com.liverowing.liverowing.R.id.*
import com.liverowing.liverowing.api.model.WorkoutType
import com.parse.ParseQuery
import kotlinx.android.synthetic.main.activity_workout_type_grid.*
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder


fun Context.WorkoutTypeGridIntent(): Intent {
    return Intent(this, WorkoutTypeGridActivity::class.java).apply {

    }
}

private const val INTENT_WORKOUT_TYPES = "workout_types"

class WorkoutTypeGridActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_type_grid)
        setSupportActionBar(a_workout_type_grid_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_workouttype_grid, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            action_single_distance,
            action_single_time,
            action_intervals -> {
                item.isChecked = !item.isChecked
                return true
            }

            action_tag_hiit,
            action_tag_cardio,
            action_tag_cross_training,
            action_tag_power,
            action_tag_weight_loss -> {
                item.isChecked = !item.isChecked
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
