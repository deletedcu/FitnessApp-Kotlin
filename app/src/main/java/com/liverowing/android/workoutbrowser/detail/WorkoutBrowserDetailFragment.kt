package com.liverowing.android.workoutbrowser.detail

import android.content.Intent
import android.os.Bundle
import android.view.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import com.liverowing.android.MainActivity
import com.liverowing.android.R
import com.liverowing.android.model.parse.User
import com.liverowing.android.model.parse.WorkoutType
import com.liverowing.android.race.RaceActivity
import kotlinx.android.synthetic.main.fragment_workout_browser_detail.*
import kotlinx.android.synthetic.main.workout_detail_collapsing_toolbar.*
import org.greenrobot.eventbus.EventBus


class WorkoutBrowserDetailFragment : MvpFragment<WorkoutBrowserDetailView, WorkoutBrowserDetailPresenter>(), WorkoutBrowserDetailView {
    private lateinit var fragmentAdapter: WorkoutBrowserDetailAdapter

    override fun createPresenter() = WorkoutBrowserDetailPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_workout_browser_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setupToolbar(workout_detail_toolbar, workout_detail_collapsing_toolbar)

        fragmentAdapter = WorkoutBrowserDetailAdapter(childFragmentManager)
        f_workout_browser_detail_container.adapter = fragmentAdapter
        f_workout_browser_detail_container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(f_workout_browser_detail_tabs))
        f_workout_browser_detail_tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(f_workout_browser_detail_container))

        f_workout_browser_detail_fab.setOnClickListener {
            val intent = Intent(activity, RaceActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun setTitle(title: String?) {
        workout_detail_toolbar.title = title
        workout_detail_collapsing_toolbar.title = title
    }

    override fun setWorkoutImage(url: String?) {
        Glide
                .with(this@WorkoutBrowserDetailFragment)
                .load(url)
                .into(workout_detail_image)
    }

    override fun setCreatedBy(createdBy: User?) {
        workout_detail_createdby.text = "Created by | ${createdBy?.username}"
        Glide
                .with(this@WorkoutBrowserDetailFragment)
                .load(createdBy?.image?.url)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(workout_detail_createdby_image)
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.workout_detail, menu)
    }
}
