package com.liverowing.android.dashboard


import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import com.liverowing.android.MainActivity
import com.liverowing.android.R
import com.liverowing.android.dashboard.quickworkout.QuickWorkoutDialogFragment
import com.liverowing.android.dashboard.quickworkout.QuickWorkoutDialogListener
import com.liverowing.android.extensions.dpToPx
import com.liverowing.android.model.parse.User
import com.liverowing.android.model.parse.WorkoutType
import com.liverowing.android.util.GridSpanDecoration
import com.liverowing.android.workoutbrowser.WorkoutBrowserFragment
import com.liverowing.android.workouthistory.CardType
import com.liverowing.android.workouthistory.DashboardAdapter
import kotlinx.android.synthetic.main.fragment_dashboard.*
import timber.log.Timber


class DashboardFragment : MvpFragment<DashboardView, DashboardPresenter>(), DashboardView, QuickWorkoutDialogListener {
    private lateinit var itemDecoration: GridSpanDecoration

    private val featuredWorkouts = mutableListOf<WorkoutType>()
    private lateinit var featuredViewManager: RecyclerView.LayoutManager
    private lateinit var featuredRecyclerView: RecyclerView
    private lateinit var featuredViewAdapter: RecyclerView.Adapter<*>
    private var allFeaturedWorkouts = mutableListOf<WorkoutType>()
    private var featuredUsers = mutableListOf<User>()
    private var selectedFeaturedUsers = mutableListOf<User>()

    private var popularWorkouts = mutableListOf<WorkoutType>()
    private lateinit var popularViewManager: RecyclerView.LayoutManager
    private lateinit var popularRecyclerView: RecyclerView
    private lateinit var popularViewAdapter: RecyclerView.Adapter<*>

    private val recentWorkouts = mutableListOf<WorkoutType>()
    private lateinit var recentViewManager: RecyclerView.LayoutManager
    private lateinit var recentRecyclerView: RecyclerView
    private lateinit var recentViewAdapter: RecyclerView.Adapter<*>


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
            val dialog = QuickWorkoutDialogFragment.newInstance(this@DashboardFragment)
            dialog.show(childFragmentManager, dialog.javaClass.toString())
        }

        f_dashboard_featured_title.setOnClickListener { v -> titleOnClick(v) }
        f_dashboard_popular_title.setOnClickListener { v -> titleOnClick(v) }
        f_dashboard_recent_title.setOnClickListener { v -> titleOnClick(v) }
        f_dashboard_featured_filter.setOnClickListener { v -> filterOnClick(v) }

        itemDecoration = GridSpanDecoration(8.dpToPx())

        featuredViewManager = GridLayoutManager(activity!!, 1, GridLayoutManager.HORIZONTAL, false)
        featuredViewAdapter = DashboardAdapter(CardType.TYPE_FEATURED, featuredWorkouts, Glide.with(this), onClick =  { _, workoutType ->
            val action = DashboardFragmentDirections.workoutBrowserDetailAction()
            action.setWorkoutType(workoutType)
            findNavController(view).navigate(action)
        }, onMoreClick = { _, workoutType ->

        })

        featuredRecyclerView = f_dashboard_featured_recyclerview.apply {
            addItemDecoration(itemDecoration)
            layoutManager = featuredViewManager
            adapter = featuredViewAdapter
        }

        popularViewManager = GridLayoutManager(activity!!, 1, GridLayoutManager.HORIZONTAL, false)
        popularViewAdapter = DashboardAdapter(CardType.TYPE_WORKOUT, popularWorkouts, Glide.with(this), onClick = { _, workoutType ->
            val action = DashboardFragmentDirections.workoutBrowserDetailAction()
            action.setWorkoutType(workoutType)
            findNavController(view).navigate(action)
        }, onMoreClick = { _, _ ->

        })

        popularRecyclerView = f_dashboard_popular_recyclerview.apply {
            addItemDecoration(itemDecoration)
            layoutManager = popularViewManager
            adapter = popularViewAdapter
        }

        recentViewManager = GridLayoutManager(activity!!, 1, GridLayoutManager.HORIZONTAL, false)
        recentViewAdapter = DashboardAdapter(CardType.TYPE_WORKOUT, recentWorkouts, Glide.with(this), onClick = { _, workoutType ->
            val action = DashboardFragmentDirections.workoutBrowserDetailAction()
            action.setWorkoutType(workoutType)
            findNavController(view).navigate(action)
        }, onMoreClick = { _, _ ->

        })

        recentRecyclerView = f_dashboard_recent_recyclerview.apply {
            addItemDecoration(itemDecoration)
            layoutManager = recentViewManager
            adapter = recentViewAdapter
        }
    }

    override fun featuredWorkoutsLoading() {

    }

    override fun featuredWorkoutsLoaded(workouts: MutableList<WorkoutType>) {
        allFeaturedWorkouts.clear()
        allFeaturedWorkouts.addAll(workouts)
        updateFeaturedWorkouts()
    }

    private fun updateFeaturedWorkouts() {
        featuredWorkouts.clear()
        if (selectedFeaturedUsers.size == 0) {

            // Reset the featuredUsers for featured filter
            featuredUsers.clear()

            var maxRotationRank = 0
            var userIds = mutableListOf<String>()

            allFeaturedWorkouts.forEach { item ->

                // add 1 of the most recent featuredUser
                if (!userIds.contains(item.createdBy!!.objectId)) {

                    // set order by rotationRank
                    val rank = item.createdBy!!.rotationRank ?: 0
                    if (rank > maxRotationRank) {
                        maxRotationRank = rank
                        featuredUsers.add(0, item.createdBy!!)
                    } else {
                        featuredUsers.add(item.createdBy!!)
                    }
                    userIds.add(item.createdBy!!.objectId)

                    // add 1 of the most recent workoutType from each featuredUser
                    featuredWorkouts.add(item)
                }
            }
        } else {

            // get featuredWorkoutTypes by filtering
            val selectedIds = selectedFeaturedUsers.map { item -> item.objectId }

            allFeaturedWorkouts.forEach { item ->
                if (selectedIds.contains(item.createdBy!!.objectId)) {
                    featuredWorkouts.add(item)
                }
            }
        }

        featuredViewAdapter.notifyDataSetChanged()
    }

    override fun featuredWorkoutsError(e: Exception) {

    }

    override fun popularWorkoutsLoading() {}

    override fun popularWorkoutsLoaded(workouts: MutableList<WorkoutType>) {
        popularWorkouts.clear()
        popularWorkouts.addAll(workouts)
        popularViewAdapter.notifyDataSetChanged()
    }

    override fun popularWorkoutsError(e: Exception) {

    }

    override fun recentWorkoutsLoading() {

    }

    override fun recentWorkoutsLoaded(workouts: MutableList<WorkoutType>) {
        recentWorkouts.clear()
        recentWorkouts.addAll(workouts)
        recentViewAdapter.notifyDataSetChanged()
    }

    override fun recentWorkoutsError(e: Exception) {

    }

    override fun deviceConnected(device: Any?) {
        Timber.d("** deviceConnected($device)")
        activity?.invalidateOptionsMenu()
    }

    override fun deviceConnecting(device: Any?) {
        Timber.d("** deviceConnecting($device)")
        activity?.invalidateOptionsMenu()
    }

    override fun deviceDisconnected(device: Any?) {
        Timber.d("** deviceDisconnected($device)")
        activity?.invalidateOptionsMenu()
    }

    override fun deviceReady(device: Any?, name: String) {
        Timber.d("** Device is ready!")
        Toast.makeText(activity, "$name connected.", Toast.LENGTH_SHORT).show()
    }

    private fun titleOnClick(v: View) {
        val category = when (v.id) {
            R.id.f_dashboard_featured_title -> WorkoutBrowserFragment.CATEGORY_FEATURED
            R.id.f_dashboard_popular_title -> WorkoutBrowserFragment.CATEGORY_RECENT_AND_LIKED
            R.id.f_dashboard_recent_title -> WorkoutBrowserFragment.CATEGORY_RECENT_AND_LIKED
            else -> WorkoutBrowserFragment.CATEGORY_FEATURED
        }

        val action = DashboardFragmentDirections.workoutBrowserAction()
        action.setCategory(category)
        findNavController(v).navigate(action)
    }

    private fun filterOnClick(v: View) {
        val dialogFragment = FeaturedChooseDialogFragment(featuredUsers, selectedFeaturedUsers, onApplyClick = { items ->
            selectedFeaturedUsers.clear()
            selectedFeaturedUsers.addAll(items)
            updateFeaturedWorkouts()
        })
        dialogFragment.show(fragmentManager, dialogFragment.javaClass.toString())
    }

    // QuickWorkoutDialogListener
    override fun onCancel(dialog: Dialog) {
        dialog.dismiss()
    }

    override fun onWorkoutTypeChoosen(dialog: Dialog, workoutTypeId: String) {
        dialog.dismiss()

        val action = DashboardFragmentDirections.raceFragmentAction()
        action.setWorkoutTypeId(workoutTypeId)
        findNavController(view!!).navigate(action)
    }
}
