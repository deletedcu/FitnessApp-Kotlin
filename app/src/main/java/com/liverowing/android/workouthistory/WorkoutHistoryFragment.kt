package com.liverowing.android.workouthistory

import android.os.Bundle
import android.view.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.liverowing.android.R.id.*
import com.liverowing.android.model.parse.Workout
import com.liverowing.android.util.Utils
import com.liverowing.android.views.PaginationScrollListener
import com.liverowing.android.workouthistory.bottomSheet.BottomSheetFragment
import com.liverowing.android.workouthistory.bottomSheet.BottomSheetListener
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.fragment_workout_history.*
import org.greenrobot.eventbus.EventBus

enum class DATETYPE {
    DAYS_7, DAYS_30, DAYS_365, DAYS_ALL
}

enum class SORTTYPE {
    DESC, ASC
}

class WorkoutHistoryFragment : MvpLceViewStateFragment<SwipeRefreshLayout, List<Workout>, WorkoutHistoryView, WorkoutHistoryPresenter>(), WorkoutHistoryView, SwipeRefreshLayout.OnRefreshListener, BottomSheetListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewDividerItemDecoration: DividerItemDecoration

    private var dataSet = mutableListOf<Workout>()
    private var workoutList = mutableListOf<Workout>()
    private val workoutTypesFilter = mutableSetOf<Int>()
    private var workoutTabType = DATETYPE.DAYS_7
    private val workoutTypesMap = hashMapOf(
            action_single_distance to 1,
            action_single_time to 2,
            action_intervals to 4
    )
    private var workoutSortType = SORTTYPE.DESC
    private var page = 0

    // Loadmore variables
    private var isLastPage = false
    private var isLoading = false

    override fun createPresenter() = WorkoutHistoryPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
    }

    override fun onDestroy() {
        super.onDestroy()
        LiveRowing.refWatcher(this.activity).watch(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workout_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).setupToolbar(f_workout_history_toolbar)

        setupUI(view)

        initData()
    }

    private fun setupUI(view: View) {
        viewManager = LinearLayoutManager(activity!!)
        viewDividerItemDecoration = DividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL)
        viewAdapter = WorkoutHistoryAdapter(workoutList, Glide.with(activity!!), onClick = { _, workout ->
            EventBus.getDefault().postSticky(workout)
            EventBus.getDefault().postSticky(workout.workoutType)
            view.findNavController().navigate(R.id.workoutHistoryDetailAction)
        }, onOptionsClick = { _, workout ->
            showBottomMenu(workout)
        })

        contentView.setOnRefreshListener(this@WorkoutHistoryFragment)

        recyclerView = f_workout_history_recyclerview.apply {
            setHasFixedSize(true)
            addItemDecoration(viewDividerItemDecoration)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        // Loadmore listener
        val linearLayoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                page ++
                loadData(false)
            }
        })

        f_workout_history_tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}

            override fun onTabUnselected(p0: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> workoutTabType = DATETYPE.DAYS_7
                    1 -> workoutTabType = DATETYPE.DAYS_30
                    2 -> workoutTabType = DATETYPE.DAYS_365
                    3 -> workoutTabType = DATETYPE.DAYS_ALL
                }
                loadData(true)
            }
        })
    }

    private fun initData() {
        when (workoutTabType) {
            DATETYPE.DAYS_7 -> f_workout_history_tabs.getTabAt(0)?.select()
            DATETYPE.DAYS_30 -> f_workout_history_tabs.getTabAt(1)?.select()
            DATETYPE.DAYS_365 -> f_workout_history_tabs.getTabAt(2)?.select()
            DATETYPE.DAYS_ALL -> f_workout_history_tabs.getTabAt(3)?.select()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.workout_history, menu)
        when (workoutSortType) {
            SORTTYPE.DESC -> menu?.findItem(action_most_recent)?.isChecked = true
            SORTTYPE.ASC -> menu?.findItem(action_oldest_first)?.isChecked = true
        }
        workoutTypesFilter.forEach {
            when (it) {
                1 -> menu?.findItem(action_single_distance)?.isChecked = true
                2 -> menu?.findItem(action_single_time)?.isChecked = true
                4 -> menu?.findItem(action_intervals)?.isChecked = true
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            action_single_distance,
            action_single_time,
            action_intervals -> {
                val type = workoutTypesMap[item.itemId]!!
                if (item.isChecked) workoutTypesFilter.remove(type) else workoutTypesFilter.add(type)
                item.isChecked = !item.isChecked

                filterValues()
                showContent()
            }
            action_most_recent -> {
                workoutSortType = SORTTYPE.DESC
                if (!item.isChecked) {
                    item.isChecked = !item.isChecked
                    loadData(true)
                }
            }
            action_oldest_first -> {
                workoutSortType = SORTTYPE.ASC
                if (!item.isChecked) {
                    item.isChecked = !item.isChecked
                    loadData(true)
                }
            }

            else -> {}
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showBottomMenu(workout: Workout) {
        val bottomSheetFragment = BottomSheetFragment.newInstance(workout, this)
        bottomSheetFragment.show(fragmentManager, "dialog")
    }

    override fun loadData(pullToRefresh: Boolean) {
        if (pullToRefresh) {
            page = 0
            isLoading = false
            isLastPage = false
        }

        val isDESC = workoutSortType == SORTTYPE.DESC
        val createdAt = when (workoutTabType) {
            DATETYPE.DAYS_7 -> Utils.getBeforeDate(7)
            DATETYPE.DAYS_30 -> Utils.getBeforeDate(30)
            DATETYPE.DAYS_365 -> Utils.getBeforeDate(365)
            DATETYPE.DAYS_ALL -> null
        }
        presenter.loadWorkouts(createdAt, isDESC, page)
    }

    override fun onRefresh() {
        loadData(true)
    }

    override fun setData(data: List<Workout>) {
        if (page == 0)
            dataSet.clear()
        dataSet.addAll(data)

        if (data.size == 0) {
            isLastPage = true
        }

        filterValues()
    }

    override fun showContent() {
        super.showContent()
        contentView.isRefreshing = false
        f_workout_history_loading.visibility = View.GONE
        isLoading = false

        if (workoutList.size == 0) {
            emptyView.visibility = View.VISIBLE
        } else {
            emptyView.visibility = View.GONE
        }
    }

    override fun showError(e: Throwable?, pullToRefresh: Boolean)  {
        super.showError(e, pullToRefresh)
        contentView.isRefreshing = false
        f_workout_history_loading.visibility = View.GONE
        isLoading = false
    }

    override fun showLoading(pullToRefresh: Boolean) {
//        super.showLoading(pullToRefresh)
        if (!pullToRefresh) {
            f_workout_history_loading.visibility = View.VISIBLE
        }
        contentView.isRefreshing = pullToRefresh
    }

    override fun createViewState(): LceViewState<List<Workout>, WorkoutHistoryView> = RetainingLceViewState()

    override fun getData(): List<Workout> = dataSet.toList()

    override fun getErrorMessage(e: Throwable?, pullToRefresh: Boolean): String {
        return e?.message!!
    }

    // Filter and refresh recyclerView
    private fun filterValues() {
        workoutList.clear()
        var data = mutableListOf<Workout>()
        // Filter by WorkoutType
        if (workoutTypesFilter.isNotEmpty()) {
            dataSet.forEach {
                if (workoutTypesFilter.contains(element = it.workoutType?.valueType)) {
                    data.add(it)
                }
            }
        } else {
            data.addAll(dataSet)
        }
        workoutList.addAll(data)
        viewAdapter.notifyDataSetChanged()
    }


    // BottomSheetFragment listener
    override fun onViewClick(workout: Workout) {

    }

    override fun onShareToFriend(workout: Workout) {

    }

    override fun onShareToSocial(workout: Workout) {

    }

    override fun onShareToConcept2(workout: Workout) {

    }

    override fun onSendToStrava(workout: Workout) {

    }

    override fun onDeleteWorkout(workout: Workout) {

    }
}
