package com.liverowing.liverowing.activity.workouttype

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import com.liverowing.liverowing.R
import com.liverowing.liverowing.R.id.*
import com.liverowing.liverowing.adapter.DashboardWorkoutTypeAdapter
import com.liverowing.liverowing.api.model.WorkoutType
import com.liverowing.liverowing.screenWidth
import com.liverowing.liverowing.util.SimpleItemDecorator
import com.parse.ParseQuery
import khronos.Dates
import khronos.Duration
import khronos.endOfDay
import khronos.minus
import kotlinx.android.synthetic.main.activity_workout_type_grid.*
import java.util.*

fun Context.WorkoutTypeGridIntent(workoutCategory: Int): Intent {
    return Intent(this, WorkoutTypeGridActivity::class.java).apply {
        putExtra(INTENT_WORKOUT_CATEGORY, workoutCategory)
    }
}

private const val INTENT_WORKOUT_CATEGORY = "workoutCategory"
const val WORKOUT_CATEGORY_FEATURED = 0
const val WORKOUT_CATEGORY_COMMUNITY = 1
const val WORKOUT_CATEGORY_RECENT_AND_LIKED = 2
const val WORKOUT_CATEGORY_AFFILIATE = 3

class WorkoutTypeGridActivity : AppCompatActivity() {
    private val workouts = mutableListOf<WorkoutType>()
    private val workoutTypes = mutableListOf<Int>()
    private val workoutTags = mutableListOf<Int>()
    private var currentTabIndex: Int = 0
    private var currentCategory: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_type_grid)
        setSupportActionBar(a_workout_type_grid_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        val cardWidth = (screenWidth()) / 2
        val cardHeight = (cardWidth * 0.80).toInt()
        a_workout_type_grid_recyclerview.apply {
            layoutManager = StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL)
            adapter = DashboardWorkoutTypeAdapter(workouts, cardWidth, cardHeight, { image, workoutType ->
                run {
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@WorkoutTypeGridActivity, image, "image")
                    this@WorkoutTypeGridActivity.startActivity(this@WorkoutTypeGridActivity.WorkoutTypeDetailIntent(workoutType), options.toBundle())
                }
            })
            addItemDecoration(SimpleItemDecorator(15))
        }
        LinearSnapHelper().attachToRecyclerView(a_workout_type_grid_recyclerview)

        a_workout_type_grid_categories.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentCategory = position

                invalidateOptionsMenu()
                workoutTypes.clear()
                a_workout_type_grid_tabbar.getTabAt(0)!!.select()
                runQueryAndPopulate()
            }
        }

        a_workout_type_grid_tabbar.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTabIndex = tab!!.position
                runQueryAndPopulate()
            }
        })


        val workoutCategory = intent.getIntExtra(INTENT_WORKOUT_CATEGORY, 0)
        a_workout_type_grid_categories.setSelection(workoutCategory, true)

    }

    private fun runQueryAndPopulate() {
        val query = when (currentCategory) {
            0 -> WorkoutType.featuredWorkouts()
            1 -> WorkoutType.communityWorkouts()
            2 -> WorkoutType.recentAndLikedWorkouts()
            3 -> WorkoutType.affiliateWorkouts()
            else -> WorkoutType.featuredWorkouts()
        }

        if (workoutTypes.size > 0) query.whereContainedIn("valueType", workoutTypes)
        for (tag in workoutTags) {
            query.whereEqualTo("filterTags." + tag, 1)
        }

        val calendar = Calendar.getInstance()
        when (currentTabIndex) {
            1 -> {
                query.whereGreaterThanOrEqualTo("createdAt", Dates.today.minus(Duration(Calendar.MONTH, 1)).endOfDay)
            }
            2 -> {
                query.whereGreaterThanOrEqualTo("likes", 10)
                query.orderByDescending("likes")
            }
        }

        query.findInBackground { objects, e ->
            if (e != null) {
                Log.d("LiveRowing", e.message)
            } else {
                workouts.clear()
                workouts.addAll(objects)
                a_workout_type_grid_recyclerview.adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.activity_workouttype_grid, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val workoutTypesMap = hashMapOf(
                action_single_distance to 1,
                action_single_time to 2,
                action_intervals to 4
        )

        val tagsMap = hashMapOf(
                action_tag_power to 0,
                action_tag_cardio to 1,
                action_tag_cross_training to 2,
                action_tag_hiit to 3,
                action_tag_speed to 4,
                action_tag_weight_loss to 5
        )


        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            action_single_distance,
            action_single_time,
            action_intervals -> {
                val type = workoutTypesMap[item.itemId]!!
                if (item.isChecked) workoutTypes.remove(type) else workoutTypes.add(type)
                item.isChecked = !item.isChecked
                runQueryAndPopulate()
                return true
            }


            action_tag_hiit,
            action_tag_cardio,
            action_tag_cross_training,
            action_tag_power,
            action_tag_speed,
            action_tag_weight_loss -> {
                val tag = tagsMap[item.itemId]!!
                if (item.isChecked) workoutTags.remove(tag) else workoutTags.add(tag)
                item.isChecked = !item.isChecked
                runQueryAndPopulate()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
