package com.liverowing.android.dashboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import com.liverowing.android.R
import com.liverowing.android.extensions.dpToPx
import com.liverowing.android.model.parse.WorkoutType
import com.liverowing.android.util.GridSpanDecoration
import com.liverowing.android.workouthistory.DashboardAdapter
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class DashboardFragment : MvpFragment<DashboardView, DashboardPresenter>(), DashboardView, View.OnClickListener {
    private lateinit var itemDecoration: GridSpanDecoration

    private val featuredWorkouts = arrayListOf<WorkoutType>()
    private lateinit var featuredWorkoutsViewManager: RecyclerView.LayoutManager
    private lateinit var featuredWorkoutsRecyclerView: RecyclerView
    private lateinit var featuredWorkoutsViewAdapter: RecyclerView.Adapter<*>

    private val recentAndLikedWorkouts = arrayListOf<WorkoutType>()
    private lateinit var recentAndLikedWorkoutsViewManager: RecyclerView.LayoutManager
    private lateinit var recentAndLikedWorkoutsRecyclerView: RecyclerView
    private lateinit var recentAndLikedWorkoutsViewAdapter: RecyclerView.Adapter<*>

    override fun createPresenter() = DashboardPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDashboard(view)
    }

    private fun setupDashboard(view: View) {
        f_dashboard_featured_workouts_title.setOnClickListener(this@DashboardFragment)
        f_dashboard_recent_and_liked_workouts_title.setOnClickListener(this@DashboardFragment)


        itemDecoration = GridSpanDecoration(8.dpToPx())

        featuredWorkoutsViewManager = GridLayoutManager(activity!!, 1, GridLayoutManager.HORIZONTAL, false)
        featuredWorkoutsViewAdapter = DashboardAdapter(580, featuredWorkouts, Glide.with(this), onClick = { _, workoutType ->
            EventBus.getDefault().postSticky(workoutType)
            //view.findNavController().navigate(R.id.action_dashboardFragment_to_workoutBrowserDetailActivity)
            view.findNavController().navigate(R.id.action_dashboardFragment_to_workoutBrowserDetailActivity)
        })

        featuredWorkoutsRecyclerView = f_dashboard_featured_workouts_recyclerview.apply {
            addItemDecoration(itemDecoration)
            layoutManager = featuredWorkoutsViewManager
            adapter = featuredWorkoutsViewAdapter
        }


        recentAndLikedWorkoutsViewManager = GridLayoutManager(activity!!, 1, GridLayoutManager.HORIZONTAL, false)
        recentAndLikedWorkoutsViewAdapter = DashboardAdapter(400, recentAndLikedWorkouts, Glide.with(this), onClick = { _, workoutType ->
            EventBus.getDefault().postSticky(workoutType)
            //view.findNavController().navigate(R.id.action_dashboardFragment_to_workoutBrowserDetailActivity)
            view.findNavController().navigate(R.id.action_dashboardFragment_to_workoutBrowserDetailActivity)
        })

        recentAndLikedWorkoutsRecyclerView = f_dashboard_recent_and_liked_workouts_recyclerview.apply {
            addItemDecoration(itemDecoration)
            layoutManager = recentAndLikedWorkoutsViewManager
            adapter = recentAndLikedWorkoutsViewAdapter
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState === null) {
            presenter.loadDashboard()
        }
    }

    override fun featuredWorkoutsLoading() {

    }

    override fun featuredWorkoutsLoaded(workouts: List<WorkoutType>) {
        featuredWorkouts.clear()
        featuredWorkouts.addAll(workouts)
        featuredWorkoutsViewAdapter.notifyDataSetChanged()
    }

    override fun featuredWorkoutsError(e: Exception) {

    }

    override fun recentAndLikedWorkoutsLoading() {

    }

    override fun recentAndLikedWorkoutsLoaded(workouts: List<WorkoutType>) {
        recentAndLikedWorkouts.clear()
        recentAndLikedWorkouts.addAll(workouts)
        recentAndLikedWorkoutsViewAdapter.notifyDataSetChanged()
    }

    override fun recentAndLikedWorkoutsError(e: Exception) {

    }

    override fun myCustomWorkoutsLoading() {

    }

    override fun myCustomWorkoutsLoaded(workouts: List<WorkoutType>) {

    }

    override fun myCustomWorkoutsError(e: Exception) {

    }

    override fun onClick(p0: View?) {
        /*val category = when (p0?.id) {
            R.id.f_dashboard_featured_workouts_title -> WORKOUT_CATEGORY_FEATURED
            R.id.f_dashboard_recent_and_liked_workouts_title -> WORKOUT_CATEGORY_RECENT_AND_LIKED
            else -> WORKOUT_CATEGORY_FEATURED
        }*/

        val bundle = Bundle().apply {
            //putInt(WorkoutBrowserFragment.INTENT_WORKOUT_CATEGORY, category)
        }
        view?.findNavController()?.navigate(R.id.action_dashboardFragment_to_workoutBrowserFragment, bundle)
    }
}
