package com.liverowing.android.dashboard


import android.os.Bundle
import android.view.*
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import com.liverowing.android.MainActivity
import com.liverowing.android.R
import com.liverowing.android.dashboard.quickworkout.QuickWorkoutDialogFragment
import com.liverowing.android.extensions.dpToPx
import com.liverowing.android.model.parse.WorkoutType
import com.liverowing.android.util.GridSpanDecoration
import com.liverowing.android.workoutbrowser.WorkoutBrowserFragment
import com.liverowing.android.workouthistory.DashboardAdapter
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.greenrobot.eventbus.EventBus


class DashboardFragment : MvpFragment<DashboardView, DashboardPresenter>(), DashboardView {
    private lateinit var itemDecoration: GridSpanDecoration

    private val featuredWorkouts = mutableListOf<WorkoutType>()
    private lateinit var featuredWorkoutsViewManager: RecyclerView.LayoutManager
    private lateinit var featuredWorkoutsRecyclerView: RecyclerView
    private lateinit var featuredWorkoutsViewAdapter: RecyclerView.Adapter<*>

    private val recentAndLikedWorkouts = mutableListOf<WorkoutType>()
    private lateinit var recentAndLikedWorkoutsViewManager: RecyclerView.LayoutManager
    private lateinit var recentAndLikedWorkoutsRecyclerView: RecyclerView
    private lateinit var recentAndLikedWorkoutsViewAdapter: RecyclerView.Adapter<*>

    override fun createPresenter() = DashboardPresenter()

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        f_dashboard_toolbar.title = ""
        (activity as MainActivity).setupToolbar(f_dashboard_toolbar)

        setupDashboard(view)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState == null && featuredWorkouts.size == 0) {
            presenter.loadDashboard()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.fragment_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_bluetooth -> findNavController(view!!).navigate(R.id.deviceScanAction)
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setupDashboard(view: View) {
        f_dashboard_fab.setOnClickListener {
            val dialog = QuickWorkoutDialogFragment()
            dialog.show(childFragmentManager, dialog.javaClass.toString())
        }

        f_dashboard_featured_workouts_title.setOnClickListener { v -> titleOnClick(v); }
        f_dashboard_recent_and_liked_workouts_title.setOnClickListener { v -> titleOnClick(v); }


        itemDecoration = GridSpanDecoration(8.dpToPx())

        featuredWorkoutsViewManager = GridLayoutManager(activity!!, 1, GridLayoutManager.HORIZONTAL, false)
        featuredWorkoutsViewAdapter = DashboardAdapter(580, featuredWorkouts, Glide.with(this)) { _, workoutType ->
            EventBus.getDefault().postSticky(workoutType)
            findNavController(view).navigate(R.id.workoutBrowserDetailAction)
        }

        featuredWorkoutsRecyclerView = f_dashboard_featured_workouts_recyclerview.apply {
            addItemDecoration(itemDecoration)
            layoutManager = featuredWorkoutsViewManager
            adapter = featuredWorkoutsViewAdapter
        }


        recentAndLikedWorkoutsViewManager = GridLayoutManager(activity!!, 1, GridLayoutManager.HORIZONTAL, false)
        recentAndLikedWorkoutsViewAdapter = DashboardAdapter(400, recentAndLikedWorkouts, Glide.with(this)) { _, workoutType ->
            EventBus.getDefault().postSticky(workoutType)
            findNavController(view).navigate(R.id.workoutBrowserDetailAction)
        }

        recentAndLikedWorkoutsRecyclerView = f_dashboard_recent_and_liked_workouts_recyclerview.apply {
            addItemDecoration(itemDecoration)
            layoutManager = recentAndLikedWorkoutsViewManager
            adapter = recentAndLikedWorkoutsViewAdapter
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

    override fun deviceConnected(device: Any) {
        activity?.invalidateOptionsMenu()
    }

    override fun deviceConnecting(device: Any) {
        activity?.invalidateOptionsMenu()
    }

    override fun deviceDisconnected(device: Any) {
        activity?.invalidateOptionsMenu()
    }

    private fun titleOnClick(v: View) {
        val category = when (v.id) {
            R.id.f_dashboard_featured_workouts_title -> WorkoutBrowserFragment.CATEGORY_FEATURED
            R.id.f_dashboard_recent_and_liked_workouts_title -> WorkoutBrowserFragment.CATEGORY_RECENT_AND_LIKED
            else -> WorkoutBrowserFragment.CATEGORY_FEATURED
        }

        val action = DashboardFragmentDirections.workoutBrowserAction()
        action.setCategory(category)
        findNavController(v).navigate(action)
    }
}
