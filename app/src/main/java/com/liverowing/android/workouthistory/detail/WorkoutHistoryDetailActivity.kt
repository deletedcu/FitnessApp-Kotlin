package com.liverowing.android.workouthistory.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import com.hannesdorfmann.mosby3.mvp.lce.MvpLceActivity
import com.liverowing.android.R
import com.liverowing.android.model.parse.Workout
import com.liverowing.android.model.parse.WorkoutType
import kotlinx.android.synthetic.main.activity_workout_history_detail.*
import kotlinx.android.synthetic.main.workout_detail_collapsing_toolbar.*
import timber.log.Timber

class WorkoutHistoryDetailActivity : MvpLceActivity<ViewPager, Workout, WorkoutHistoryDetailView, WorkoutHistoryDetailPresenter>(), WorkoutHistoryDetailView {
    private lateinit var fragmentAdapter: WorkoutHistoryDetailAdapter
    private var workout: Workout? = null
    private var workoutType: WorkoutType? = null

    override fun createPresenter(): WorkoutHistoryDetailPresenter {
        return WorkoutHistoryDetailPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_history_detail)

        setSupportActionBar(workout_detail_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fragmentAdapter = WorkoutHistoryDetailAdapter(supportFragmentManager)
        contentView.adapter = fragmentAdapter
        contentView.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(a_workout_history_detail_tabs))
        a_workout_history_detail_tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(contentView))

        loadData(false)
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

    override fun setData(data: Workout?) {
        Timber.d("** setData")
        workout = data
        workoutType = workout?.workoutType

        supportActionBar?.title = workoutType?.name
        workout_detail_collapsing_toolbar.title = workoutType?.name
        workout_detail_createdby.text = "Created by | ${workoutType?.createdBy?.username}"

        Glide
                .with(this@WorkoutHistoryDetailActivity)
                .load(workoutType?.image?.url)
                .into(workout_detail_image)

        Glide
                .with(this@WorkoutHistoryDetailActivity)
                .load(workoutType?.createdBy?.image?.url)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(workout_detail_createdby_image)
    }

    override fun loadData(pullToRefresh: Boolean) {
        Timber.d("** loadData")

        val appLinkAction = intent?.action
        val appLinkData = intent?.data

        if (Intent.ACTION_VIEW == appLinkAction && appLinkData !== null) {
            val workoutObjectId = appLinkData.lastPathSegment
            presenter.getWorkout(workoutObjectId)
        } else {
            presenter.getWorkout()
        }
    }

    override fun getErrorMessage(e: Throwable?, pullToRefresh: Boolean): String {
        Timber.d("** getErrorMessage")
        return e?.message!!
    }
}
