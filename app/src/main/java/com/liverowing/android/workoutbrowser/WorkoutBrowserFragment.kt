package com.liverowing.android.workoutbrowser

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.LceViewState
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.MvpLceViewStateFragment
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.data.RetainingLceViewState
import com.liverowing.android.LiveRowing
import com.liverowing.android.MainActivity
import com.liverowing.android.R
import com.liverowing.android.extensions.dpToPx
import com.liverowing.android.extensions.toggleVisibility
import com.liverowing.android.model.parse.User
import com.liverowing.android.model.pm.FilterItem
import com.liverowing.android.util.GridSpanDecoration
import com.parse.ParseObject
import kotlinx.android.synthetic.main.fragment_workout_browser.*
import kotlinx.android.synthetic.main.workout_browser_backdrop.*


class WorkoutBrowserFragment : MvpLceViewStateFragment<LinearLayout, List<ParseObject>, WorkoutBrowserView, WorkoutBrowserPresenter>(), WorkoutBrowserView {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: WorkoutBrowserAdapter
    private lateinit var viewManager: GridLayoutManager
    private lateinit var viewDividerItemDecoration: GridSpanDecoration

    private var dataSet = mutableListOf<ParseObject>()

    private lateinit var filterItemDecoration: GridSpanDecoration

    private lateinit var filterGroupByRecyclerView: RecyclerView
    private lateinit var filterCreatedByRecyclerView: RecyclerView
    private lateinit var filterWorkoutTypesRecyclerView: RecyclerView
    private lateinit var filterShowOnlyRecyclerView: RecyclerView
    private lateinit var filterTagsRecyclerView: RecyclerView

    private lateinit var filterGroupByAdapter: WorkoutBrowserFilterAdapter
    private lateinit var filterCreatedByAdapter: WorkoutBrowserFilterAdapter
    private lateinit var filterWorkoutTypesAdapter: WorkoutBrowserFilterAdapter
    private lateinit var filterShowOnlyAdapter: WorkoutBrowserFilterAdapter
    private lateinit var filterTagsAdapter: WorkoutBrowserFilterAdapter

    private var filterGroupBySelectedItems = mutableListOf<FilterItem>(FilterItem.defaultGroupByItem())
    private var filterCreatedBySelectedItems = mutableListOf<FilterItem>()
    private var filterWorkoutTypesSelectedItems = mutableListOf<FilterItem>()
    private var filterShowOnlySelectedItems = mutableListOf<FilterItem>()
    private var filterTagsSelectedItems = mutableListOf<FilterItem>()
    private var featuredUsers = mutableListOf<FilterItem>()

    // Backdrop menu values
    private var backdropShown = false
    private val animatorSet = AnimatorSet()
    private val interpolator = AccelerateDecelerateInterpolator()
    private var height: Int = 0

    private var isFilterChanged = false

    override fun createPresenter() = WorkoutBrowserPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        height = displayMetrics.heightPixels
    }

    override fun onDestroy() {
        super.onDestroy()
        LiveRowing.refWatcher(this.activity).watch(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workout_browser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setupToolbar(f_workout_browser_toolbar)

        viewManager = GridLayoutManager(activity!!, 2)
        viewDividerItemDecoration = GridSpanDecoration(8.dpToPx())
        viewAdapter = WorkoutBrowserAdapter(dataSet, Glide.with(activity!!)) { _, workoutType ->
            val action = WorkoutBrowserFragmentDirections.workoutBrowserDetailAction()
            action.setWorkoutType(workoutType)
            Navigation.findNavController(view).navigate(action)
        }
        viewManager.spanSizeLookup = viewAdapter.spanSizeLookup

        recyclerView = f_workout_browser_recyclerview.apply {
            setHasFixedSize(true)
            addItemDecoration(viewDividerItemDecoration)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        recyclerView.isNestedScrollingEnabled = false

        f_workout_browser_toolbar.setNavigationOnClickListener {
            if (backdropShown) {
                onToggleBackdropMenu()
            } else {
                fragmentManager!!.popBackStack()
            }
        }

        f_workout_browser_filter.setOnClickListener {
            if (backdropShown) {
                resetFilters()
            } else {
                onToggleBackdropMenu()
            }
        }

        f_workout_browser_subtitle.setOnClickListener {
            onToggleBackdropMenu()
        }

        filterItemDecoration = GridSpanDecoration(0, 0, 8.dpToPx(), 0)

        filterGroupByAdapter = WorkoutBrowserFilterAdapter(FilterItem.groupByItems(), filterGroupBySelectedItems, false, true, onSelectChanged = { selectedItems ->
            filterGroupBySelectedItems = selectedItems
            presenter.category = selectedItems
            isFilterChanged = true
        })

        filterCreatedByAdapter = WorkoutBrowserFilterAdapter(featuredUsers, filterCreatedBySelectedItems, true, false, onSelectChanged = { selectedItems ->
            filterCreatedBySelectedItems = selectedItems
            presenter.createdBy = selectedItems
            isFilterChanged = true
        })

        filterWorkoutTypesAdapter = WorkoutBrowserFilterAdapter(FilterItem.workoutTypeItems(), filterWorkoutTypesSelectedItems, true, false, onSelectChanged = { selectedItems ->
            filterWorkoutTypesSelectedItems = selectedItems
            presenter.types = selectedItems
            isFilterChanged = true
        })

        filterShowOnlyAdapter = WorkoutBrowserFilterAdapter(FilterItem.showOnlyItems(), filterShowOnlySelectedItems, false, false, onSelectChanged = { selectedItems ->
            filterShowOnlySelectedItems = selectedItems
            presenter.filter = selectedItems
            isFilterChanged = true
        })

        filterTagsAdapter = WorkoutBrowserFilterAdapter(FilterItem.tagItems(), filterTagsSelectedItems, true, false, onSelectChanged = { selectedItems ->
            filterTagsSelectedItems = selectedItems
            presenter.tags = selectedItems
            isFilterChanged = true
        })

        filterGroupByRecyclerView = backdrop_groupby_recyclerview.apply {
            addItemDecoration(filterItemDecoration)
            layoutManager = GridLayoutManager(activity!!, 1, GridLayoutManager.HORIZONTAL, false)
            adapter = filterGroupByAdapter
        }

        filterCreatedByRecyclerView = backdrop_createdby_recyclerview.apply {
            addItemDecoration(filterItemDecoration)
            layoutManager = GridLayoutManager(activity!!, 1, GridLayoutManager.HORIZONTAL, false)
            adapter = filterCreatedByAdapter
        }

        filterWorkoutTypesRecyclerView = backdrop_workouttypes_recyclerview.apply {
            addItemDecoration(filterItemDecoration)
            layoutManager = GridLayoutManager(activity!!, 1, GridLayoutManager.HORIZONTAL, false)
            adapter = filterWorkoutTypesAdapter
        }

        filterShowOnlyRecyclerView = backdrop_showonly_recyclerview.apply {
            addItemDecoration(filterItemDecoration)
            layoutManager = GridLayoutManager(activity!!, 1, GridLayoutManager.HORIZONTAL, false)
            adapter = filterShowOnlyAdapter
        }

        filterTagsRecyclerView = backdrop_tags_recyclerview.apply {
            addItemDecoration(filterItemDecoration)
            layoutManager = GridLayoutManager(activity!!, 1, GridLayoutManager.HORIZONTAL, false)
            adapter = filterTagsAdapter
        }

    }

    override fun loadData(pullToRefresh: Boolean) {
        presenter.loadWorkoutTypes(pullToRefresh)
    }

    override fun setFeaturedUsers(users: List<User>) {
        featuredUsers.clear()
        var index = 0
        users.forEach {
            featuredUsers.add(FilterItem(index++, it.username, it.objectId))
        }
        filterCreatedByAdapter.notifyDataSetChanged()
    }

    override fun createViewState(): LceViewState<List<ParseObject>, WorkoutBrowserView> = RetainingLceViewState()

    override fun setData(data: List<ParseObject>) {
        dataSet.clear()
        dataSet.addAll(data)
        viewAdapter.notifyDataSetChanged()
    }

    override fun getData(): List<ParseObject> {
        return dataSet.toList()
    }

    override fun getErrorMessage(e: Throwable?, pullToRefresh: Boolean): String {
        return e?.message!!
    }

    override fun showError(e: Throwable?, pullToRefresh: Boolean) {
        super.showError(e, pullToRefresh)
        loadingView.visibility = View.GONE
    }

    override fun showLoading(pullToRefresh: Boolean) {
        super.showLoading(pullToRefresh)
        loadingView.visibility = View.VISIBLE
    }

    override fun showContent() {
        super.showContent()
        loadingView.visibility = View.GONE
    }

    private fun onToggleBackdropMenu() {
        backdropShown = !backdropShown

        // Cancel the existing animations
        animatorSet.removeAllListeners()
        animatorSet.end()
        animatorSet.cancel()

        updateIcon()
        if (backdropShown) {
            product_grid.scrollTo(0, 0)
        }

        val translateY = height - context!!.resources.getDimensionPixelSize(R.dimen.product_grid_reveal_height)

        val animator = ObjectAnimator.ofFloat(product_grid, "translationY", (if (backdropShown) translateY else 0).toFloat())
        animator.duration = 500
        if (interpolator != null) {
            animator.interpolator = interpolator
        }
        animatorSet.play(animator)
        animator.start()

        if (!backdropShown && isFilterChanged) {
            presenter.loadWorkoutTypes(false)
            isFilterChanged = false
        }
    }

    private fun updateIcon() {
        f_workout_browser_subtitle.toggleVisibility()
        if (backdropShown) {
            f_workout_browser_toolbar.title = "FILTERS"
            f_workout_browser_toolbar.setNavigationIcon(R.drawable.ic_close)
            f_workout_browser_filter.setImageResource(R.drawable.ic_delete)
        } else {
            f_workout_browser_toolbar.title = "WORKOUTS"
            f_workout_browser_toolbar.setNavigationIcon(R.drawable.ic_back)
            f_workout_browser_filter.setImageResource(R.drawable.ic_tune)
        }
    }

    private fun resetFilters() {
        filterGroupBySelectedItems.clear()
        filterGroupBySelectedItems.add(FilterItem.defaultGroupByItem())
        filterCreatedBySelectedItems.clear()
        filterWorkoutTypesSelectedItems.clear()
        filterShowOnlySelectedItems.clear()
        filterTagsSelectedItems.clear()
        presenter.reset()

        filterGroupByAdapter.notifyDataSetChanged()
        filterCreatedByAdapter.notifyDataSetChanged()
        filterWorkoutTypesAdapter.notifyDataSetChanged()
        filterShowOnlyAdapter.notifyDataSetChanged()
        filterTagsAdapter.notifyDataSetChanged()
    }
}
