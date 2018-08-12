package com.liverowing.android.workouthistory.detail

import android.os.Bundle
import android.view.*
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import com.hannesdorfmann.mosby3.mvp.lce.MvpLceFragment
import com.liverowing.android.MainActivity
import com.liverowing.android.R
import com.liverowing.android.model.parse.Workout
import com.liverowing.android.model.parse.WorkoutType
import kotlinx.android.synthetic.main.fragment_workout_history_detail.*
import kotlinx.android.synthetic.main.workout_detail_collapsing_toolbar.*
import timber.log.Timber

class WorkoutHistoryDetailFragment : MvpLceFragment<ViewPager, Workout, WorkoutHistoryDetailView, WorkoutHistoryDetailPresenter>(), WorkoutHistoryDetailView {
    private lateinit var fragmentAdapter: WorkoutHistoryDetailAdapter
    private var workout: Workout? = null
    private var workoutType: WorkoutType? = null

    override fun createPresenter(): WorkoutHistoryDetailPresenter {
        return WorkoutHistoryDetailPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_workout_history_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setupToolbar(workout_detail_toolbar, workout_detail_collapsing_toolbar)

        fragmentAdapter = WorkoutHistoryDetailAdapter(childFragmentManager)
        contentView.adapter = fragmentAdapter
        contentView.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(a_workout_history_detail_tabs))
        a_workout_history_detail_tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(contentView))

        loadData(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.workout_detail, menu)
    }

    override fun setData(data: Workout?) {
        Timber.d("** setData")
        workout = data
        workoutType = workout?.workoutType

        workout_detail_toolbar.title = workoutType?.name
        workout_detail_collapsing_toolbar.title = workoutType?.name
        workout_detail_createdby.text = "Created by | ${workoutType?.createdBy?.username}"

        Glide
                .with(this@WorkoutHistoryDetailFragment)
                .load(workoutType?.image?.url)
                .into(workout_detail_image)

        Glide
                .with(this@WorkoutHistoryDetailFragment)
                .load(workoutType?.createdBy?.image?.url)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(workout_detail_createdby_image)
    }

    override fun loadData(pullToRefresh: Boolean) {
        Timber.d("** loadData")
        presenter.getWorkout()
    }

    override fun getErrorMessage(e: Throwable?, pullToRefresh: Boolean): String {
        Timber.d("** getErrorMessage")
        return e?.message!!
    }
}
