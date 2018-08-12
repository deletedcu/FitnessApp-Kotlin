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
import com.liverowing.android.MainActivity
import com.liverowing.android.R
import com.liverowing.android.extensions.dpToPx
import com.liverowing.android.model.parse.WorkoutType
import com.liverowing.android.util.GridSpanDecoration
import com.liverowing.android.workoutbrowser.WorkoutBrowserFragment
import com.liverowing.android.workouthistory.DashboardAdapter
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.greenrobot.eventbus.EventBus

class DashboardFragment : MvpFragment<DashboardView, DashboardPresenter>(), DashboardView {
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
        (activity as MainActivity).setupToolbar(f_dashboard_toolbar)
        setupDashboard(view)
    }

    private fun setupDashboard(view: View) {
        f_dashboard_fab.setOnClickListener { v -> fabOnClick(v) }
        f_dashboard_fab_just_row.setOnClickListener { v -> fabOnClick(v) }
        f_dashboard_fab_single_distance.setOnClickListener { v -> fabOnClick(v) }
        f_dashboard_fab_single_time.setOnClickListener { v -> fabOnClick(v) }
        f_dashboard_fab_background.setOnClickListener { v -> fabOnClick(v) }
        f_dashboard_featured_workouts_title.setOnClickListener { v -> titleOnClick(v); }
        f_dashboard_recent_and_liked_workouts_title.setOnClickListener { v -> titleOnClick(v); }


        itemDecoration = GridSpanDecoration(8.dpToPx())

        featuredWorkoutsViewManager = GridLayoutManager(activity!!, 1, GridLayoutManager.HORIZONTAL, false)
        featuredWorkoutsViewAdapter = DashboardAdapter(580, featuredWorkouts, Glide.with(this)) { _, workoutType ->
            EventBus.getDefault().postSticky(workoutType)
            view.findNavController().navigate(R.id.workoutBrowserDetailAction)
        }

        featuredWorkoutsRecyclerView = f_dashboard_featured_workouts_recyclerview.apply {
            addItemDecoration(itemDecoration)
            layoutManager = featuredWorkoutsViewManager
            adapter = featuredWorkoutsViewAdapter
        }


        recentAndLikedWorkoutsViewManager = GridLayoutManager(activity!!, 1, GridLayoutManager.HORIZONTAL, false)
        recentAndLikedWorkoutsViewAdapter = DashboardAdapter(400, recentAndLikedWorkouts, Glide.with(this)) { _, workoutType ->
            EventBus.getDefault().postSticky(workoutType)
            view.findNavController().navigate(R.id.workoutBrowserDetailAction)
        }

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


    private var mFabOpen: Boolean = false
    private fun fabOnClick(v: View) {
        when (v.id) {
            R.id.f_dashboard_fab_background,
            R.id.f_dashboard_fab -> if (mFabOpen) closeFabMenu() else showFabMenu()

            R.id.f_dashboard_fab_just_row -> {
            }
            R.id.f_dashboard_fab_single_distance -> {
            }
            R.id.action_single_time -> {
            }
        }
    }

    private fun showFabMenu() {
        mFabOpen = true
        f_dashboard_fab_just_row.visibility = View.VISIBLE
        f_dashboard_fab_single_distance.visibility = View.VISIBLE
        f_dashboard_fab_single_time.visibility = View.VISIBLE

        f_dashboard_fab_background.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f)
        }

        f_dashboard_fab_single_time.animate().translationY((-65f).dpToPx())
        f_dashboard_fab_single_distance.animate().translationY((-120f).dpToPx())
        f_dashboard_fab_just_row.animate().translationY((-175f).dpToPx())
    }

    private fun closeFabMenu() {
        mFabOpen = false

        f_dashboard_fab_background.animate().alpha(0f).withEndAction { f_dashboard_fab_background.visibility = View.GONE }
        f_dashboard_fab_single_time.animate().translationY(0f)
        f_dashboard_fab_single_distance.animate().translationY(0f)
        f_dashboard_fab_just_row.animate().translationY(0f).withEndAction {
            f_dashboard_fab_single_time.visibility = View.GONE
            f_dashboard_fab_single_distance.visibility = View.GONE
            f_dashboard_fab_just_row.visibility = View.GONE
        }
    }

    private fun titleOnClick(v: View) {
        val category = when (v.id) {
            R.id.f_dashboard_featured_workouts_title -> WorkoutBrowserFragment.CATEGORY_FEATURED
            R.id.f_dashboard_recent_and_liked_workouts_title -> WorkoutBrowserFragment.CATEGORY_RECENT_AND_LIKED
            else -> WorkoutBrowserFragment.CATEGORY_FEATURED
        }

        val action = DashboardFragmentDirections.workoutBrowserAction()
        action.setCategory(category)
        v.findNavController().navigate(action)
    }
}
