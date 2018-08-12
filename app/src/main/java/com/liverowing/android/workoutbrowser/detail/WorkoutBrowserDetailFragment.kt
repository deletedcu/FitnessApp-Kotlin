package com.liverowing.android.workoutbrowser.detail

import android.os.Bundle
import android.view.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import com.liverowing.android.MainActivity
import com.liverowing.android.R
import com.liverowing.android.model.parse.WorkoutType
import kotlinx.android.synthetic.main.fragment_workout_browser_detail.*
import kotlinx.android.synthetic.main.workout_detail_collapsing_toolbar.*
import org.greenrobot.eventbus.EventBus


class WorkoutBrowserDetailFragment : MvpFragment<WorkoutBrowserDetailView, WorkoutBrowserDetailPresenter>(), WorkoutBrowserDetailView {
    private lateinit var fragmentAdapter: WorkoutBrowserDetailAdapter
    private lateinit var workoutType: WorkoutType

    override fun createPresenter(): WorkoutBrowserDetailPresenter {
        return WorkoutBrowserDetailPresenter()
    }

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
    }

    override fun onResume() {
        super.onResume()

        workoutType = EventBus.getDefault().getStickyEvent(WorkoutType::class.java)

        workout_detail_toolbar.title = workoutType.name
        workout_detail_collapsing_toolbar.title = workoutType.name
        workout_detail_createdby.text = "Created by | ${workoutType.createdBy?.username}"

        Glide
                .with(this@WorkoutBrowserDetailFragment)
                .load(workoutType.image?.url)
                .into(workout_detail_image)

        Glide
                .with(this@WorkoutBrowserDetailFragment)
                .load(workoutType.createdBy?.image?.url)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .into(workout_detail_createdby_image)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.workout_detail, menu)
    }
}
