package com.liverowing.android.workoutbrowser

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.get
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.LceViewState
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.MvpLceViewStateFragment
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.data.RetainingLceViewState
import com.liverowing.android.LiveRowing
import com.liverowing.android.MainActivity
import com.liverowing.android.R
import com.liverowing.android.extensions.dpToPx
import com.liverowing.android.extensions.toggleVisibility
import com.liverowing.android.util.GridSpanDecoration
import com.parse.ParseObject
import kotlinx.android.synthetic.main.fragment_workout_browser.*


class WorkoutBrowserFragment : MvpLceViewStateFragment<SwipeRefreshLayout, List<ParseObject>, WorkoutBrowserView, WorkoutBrowserPresenter>(), WorkoutBrowserView, SwipeRefreshLayout.OnRefreshListener, TabLayout.BaseOnTabSelectedListener<TabLayout.Tab> {
    companion object {
        const val CATEGORY_FEATURED = 0
        const val CATEGORY_COMMUNITY = 1
        const val CATEGORY_RECENT = 2
        const val CATEGORY_MY_CUSTOM = 3
        const val CATEGORY_AFFILIATE = 4

        const val FILTER_ALL = 0
        const val FILTER_NEW = 1
        const val FILTER_POPULAR = 2
        const val FILTER_COMPLETED = 3
        const val FILTER_NOT_COMPLETED = 4

        const val TYPE_SINGLE_DISTANCE = 1
        const val TYPE_SINGLE_TIME = 2
        const val TYPE_INTERVALS = 4

        const val TAG_POWER = 0
        const val TAG_CARDIO = 1
        const val TAG_CROSS_TRAINING = 2
        const val TAG_HIIT = 3
        const val TAG_SPEED = 4
        const val TAG_WEIGHT_LOSS = 5
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: WorkoutBrowserAdapter
    private lateinit var viewManager: GridLayoutManager
    private lateinit var viewDividerItemDecoration: GridSpanDecoration

    private var dataSet = mutableListOf<ParseObject>()

    // Backdrop menu values
    private var backdropShown = false
    private val animatorSet = AnimatorSet()
    private val interpolator = AccelerateDecelerateInterpolator()
    private var height: Int = 0


    override fun createPresenter() = WorkoutBrowserPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
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

//        f_workout_browser_filter_tabs.apply {
//            getTabAt(presenter.filter)?.select()
//            addOnTabSelectedListener(this@WorkoutBrowserFragment)
//        }
//
//        f_workout_browser_category_tabs.apply {
//            getTabAt(presenter.category)?.select()
//            addOnTabSelectedListener(this@WorkoutBrowserFragment)
//        }

        viewManager = GridLayoutManager(activity!!, 2)
        viewDividerItemDecoration = GridSpanDecoration(8.dpToPx())
        viewAdapter = WorkoutBrowserAdapter(dataSet, Glide.with(activity!!)) { _, workoutType ->
            val action = WorkoutBrowserFragmentDirections.workoutBrowserDetailAction()
            action.setWorkoutType(workoutType)
            Navigation.findNavController(view).navigate(action)
        }
        viewManager.spanSizeLookup = viewAdapter.spanSizeLookup

        contentView.setOnRefreshListener(this@WorkoutBrowserFragment)
        recyclerView = f_workout_browser_recyclerview.apply {
            setHasFixedSize(true)
            addItemDecoration(viewDividerItemDecoration)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        f_workout_browser_toolbar.setNavigationOnClickListener {
            if (backdropShown) {
                onToggleBackdropMenu()
            } else {
                fragmentManager!!.popBackStack()
            }
        }

        f_workout_browser_filter.setOnClickListener {
            if (backdropShown) {

            } else {
                onToggleBackdropMenu()
            }
        }

        f_workout_browser_subtitle.setOnClickListener {
            onToggleBackdropMenu()
        }

    }

    override fun onNewViewStateInstance() {
        val args = WorkoutBrowserFragmentArgs.fromBundle(arguments)

        args.tags?.split(",")?.forEach { presenter.tags.add(it.toInt()) }
        args.types?.split(",")?.forEach { presenter.types.add(it.toInt()) }
//        f_workout_browser_category_tabs.getTabAt(args.category)?.select()
//        f_workout_browser_filter_tabs.getTabAt(args.filter)?.select()

        if (args.category + args.filter == 0 || args.tags != null || args.types != null) {
            loadData(false)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

//        val items = menu.findItem(R.id.action_filter).subMenu!!
//        for (i in 0..2) {
//            items[i].isChecked = presenter.types.contains(i)
//        }
//
//        for (i in 0..5) {
//            items[5 + i].isChecked = presenter.tags.contains(i)
//        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
//        inflater?.inflate(R.menu.workout_browser, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val checked = item.isChecked
        if (item.order < 10) {
            item.isChecked = !checked
            if (checked) presenter.types.remove(item.order) else presenter.types.add(item.order)
            presenter.loadWorkoutTypes(false)
        } else if (item.order < 20) {
            item.isChecked = !checked
            if (checked) presenter.tags.remove(item.order - 10) else presenter.tags.add(item.order - 10)
            presenter.loadWorkoutTypes(false)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun loadData(pullToRefresh: Boolean) {
        presenter.loadWorkoutTypes(pullToRefresh)
    }

    override fun showContent() {
        super.showContent()
        contentView.isRefreshing = false
    }

    override fun showError(e: Throwable?, pullToRefresh: Boolean) {
        super.showError(e, pullToRefresh)
        contentView.isRefreshing = false
    }

    override fun showLoading(pullToRefresh: Boolean) {
        super.showLoading(pullToRefresh)
        contentView.isRefreshing = pullToRefresh
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

    override fun onRefresh() {
        loadData(true)
    }

    // TabOnSelectedListener
    override fun onTabReselected(tab: TabLayout.Tab) {}

    override fun onTabUnselected(tab: TabLayout.Tab) {}
    override fun onTabSelected(tab: TabLayout.Tab) {
//        if (tab.parent.id == R.id.f_workout_browser_category_tabs) {
//            presenter.reset()
//            activity?.invalidateOptionsMenu()
//            presenter.category = tab.position
//
//            f_workout_browser_filter_tabs.apply {
//                removeOnTabSelectedListener(this@WorkoutBrowserFragment)
//                getTabAt(0)?.select()
////                addOnTabSelectedListener(this@WorkoutBrowserFragment)
//            }
//        } else if (tab.parent.id == R.id.f_workout_browser_filter_tabs) {
//            presenter.filter = tab.position
//        }
//        presenter.loadWorkoutTypes(false)
    }

    private fun onToggleBackdropMenu() {
        backdropShown = !backdropShown

        // Cancel the existing animations
        animatorSet.removeAllListeners()
        animatorSet.end()
        animatorSet.cancel()

        updateIcon()

        val translateY = height - context!!.resources.getDimensionPixelSize(R.dimen.product_grid_reveal_height)

        val animator = ObjectAnimator.ofFloat(product_grid, "translationY", (if (backdropShown) translateY else 0).toFloat())
        animator.duration = 500
        if (interpolator != null) {
            animator.interpolator = interpolator
        }
        animatorSet.play(animator)
        animator.start()
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
}
