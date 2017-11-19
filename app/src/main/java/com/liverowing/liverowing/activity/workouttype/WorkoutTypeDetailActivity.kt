package com.liverowing.liverowing.activity.workouttype

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.liverowing.liverowing.LiveRowing.Companion.eventBus
import com.liverowing.liverowing.R
import com.liverowing.liverowing.model.parse.WorkoutType
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.activity_workout_type_detail.*

fun Context.WorkoutTypeDetailIntent(workoutType: WorkoutType): Intent {
    Log.d("LiveRowing", "WorkoutTypeDetailIntent: " + workoutType.createdBy?.username)
    return Intent(this, WorkoutTypeDetailActivity::class.java).apply {
    }
}

private const val INTENT_WORKOUT_TYPE = "workout_type"

class WorkoutTypeDetailActivity : AppCompatActivity() {
    private lateinit var mSectionsPagerAdapter: SectionsPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_type_detail)

        setSupportActionBar(a_workout_type_grid_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        a_workout_type_detail_container.adapter = mSectionsPagerAdapter
        a_workout_type_detail_container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(a_workout_type_detail_tabs))
        a_workout_type_detail_tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(a_workout_type_detail_container))

        eventBus.register(this)
    }

    override fun onStop() {
        super.onStop()
        eventBus.unregister(this)
    }

    @Subscribe
    fun onReceiveWorkoutType(workoutType: WorkoutType) {
        supportActionBar?.title = workoutType.name
        if (workoutType.image?.url != null) {
            Glide.with(this).load(workoutType.image?.url).into(a_workout_type_detail_image)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_workout_type_detail, menu)
        return true
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

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> WorkoutTypeDetailsFragment.newInstance()
                1 -> WorkoutTypeLeadersAndStatsFragment.newInstance()
                else -> Fragment()
            }
        }

        override fun getCount(): Int {
            return 2
        }
    }

}
