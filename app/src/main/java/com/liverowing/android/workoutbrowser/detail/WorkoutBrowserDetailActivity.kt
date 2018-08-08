package com.liverowing.android.workoutbrowser.detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import com.liverowing.android.R
import com.liverowing.android.model.parse.WorkoutType
import kotlinx.android.synthetic.main.activity_workout_browser_detail.*
import kotlinx.android.synthetic.main.workout_detail_collapsing_toolbar.*
import org.greenrobot.eventbus.EventBus


class WorkoutBrowserDetailActivity : MvpActivity<WorkoutBrowserDetailView, WorkoutBrowserDetailPresenter>() {
    private lateinit var fragmentAdapter: WorkoutBrowserDetailAdapter
    private lateinit var workoutType: WorkoutType

    override fun createPresenter(): WorkoutBrowserDetailPresenter {
        return WorkoutBrowserDetailPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_browser_detail)

        setSupportActionBar(workout_detail_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fragmentAdapter = WorkoutBrowserDetailAdapter(supportFragmentManager)
        a_workout_browser_detail_container.adapter = fragmentAdapter
        a_workout_browser_detail_container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(a_workout_browser_detail_tabs))
        a_workout_browser_detail_tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(a_workout_browser_detail_container))
    }

    override fun onResume() {
        super.onResume()

        workoutType = EventBus.getDefault().getStickyEvent(WorkoutType::class.java)
        supportActionBar?.title = workoutType.name
        workout_detail_collapsing_toolbar.title = workoutType.name
        workout_detail_createdby.text = "Created by | ${workoutType.createdBy?.username}"

        Glide
                .with(this@WorkoutBrowserDetailActivity)
                .load(workoutType.image?.url)
                .into(workout_detail_image)

        Glide
                .with(this@WorkoutBrowserDetailActivity)
                .load(workoutType?.createdBy?.image?.url)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(workout_detail_createdby_image)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.workout_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
