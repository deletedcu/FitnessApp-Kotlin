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
import com.liverowing.android.workouthistory.bottomSheet.BottomSheetFragment
import com.liverowing.android.workouthistory.bottomSheet.BottomSheetListener
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
    private var workoutHistories = mutableListOf<Workout>()
    private val workoutTypesFilter = mutableSetOf<Int>()
    private var workoutTabType = DATETYPE.DAYS_7
    private val workoutTypesMap = hashMapOf(
            action_single_distance to 1,
            action_single_time to 2,
            action_intervals to 4
    )
    private var workoutSortType = SORTTYPE.DESC

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

        viewManager = LinearLayoutManager(activity!!)
        viewDividerItemDecoration = DividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL)
        viewAdapter = WorkoutHistoryAdapter(dataSet, Glide.with(activity!!), onClick = { _, workout ->
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
                filterValues()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.workout_history, menu)
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
            }
            action_most_recent -> {
                workoutSortType = SORTTYPE.DESC
                if (!item.isChecked) {
                    item.isChecked = !item.isChecked
                    filterValues()
                }
            }
            action_oldest_first -> {
                workoutSortType = SORTTYPE.ASC
                if (!item.isChecked) {
                    item.isChecked = !item.isChecked
                    filterValues()
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
        presenter.loadWorkouts(pullToRefresh)
    }

    override fun onRefresh() {
        loadData(true)
    }

    override fun setData(data: List<Workout>) {
        workoutHistories.clear()
        workoutHistories.addAll(data)

        filterValues()
    }

    private fun updateAdapter(data: List<Workout>) {
        dataSet.clear()
        dataSet.addAll(data)

        viewAdapter.notifyDataSetChanged()
    }

    override fun showContent() {
        super.showContent()
        contentView.isRefreshing = false
    }

    override fun showError(e: Throwable?, pullToRefresh: Boolean)  {
        super.showError(e, pullToRefresh)
        contentView.isRefreshing = false
    }

    override fun showLoading(pullToRefresh: Boolean) {
        super.showLoading(pullToRefresh)
        contentView.isRefreshing = pullToRefresh
    }

    override fun createViewState(): LceViewState<List<Workout>, WorkoutHistoryView> = RetainingLceViewState()

    override fun getData(): List<Workout> = dataSet.toList()

    override fun getErrorMessage(e: Throwable?, pullToRefresh: Boolean): String {
        return "There was an error loading workout history:\n\n${e?.message}"
    }

    private fun filterValues() {
        val sortData = sortValues()
        var data = mutableListOf<Workout>()
        // Filter by WorkoutType
        if (workoutTypesFilter.isNotEmpty()) {
            sortData.forEach {
                if (workoutTypesFilter.contains(element = it.workoutType?.valueType)) {
                    data.add(it)
                }
            }
        } else {
            data.addAll(sortData)
        }

        // Filter by tab
        var filterData = mutableListOf<Workout>()
        when (workoutTabType) {
            DATETYPE.DAYS_7 -> {
                val date = Utils.getBeforeDate(7)
                data.forEach {
                    if (it.createdAt.compareTo(date) > 0) {
                        filterData.add(it)
                    }
                }
            }
            DATETYPE.DAYS_30 -> {
                val date = Utils.getBeforeDate(30)
                data.forEach {
                    if (it.createdAt.compareTo(date) > 0) {
                        filterData.add(it)
                    }
                }
            }
            DATETYPE.DAYS_365 -> {
                val date = Utils.getBeforeDate(365)
                data.forEach {
                    if (it.createdAt.compareTo(date) > 0) {
                        filterData.add(it)
                    }
                }
            }
            DATETYPE.DAYS_ALL -> {
                filterData.addAll(data)
            }
        }

        updateAdapter(filterData)
    }

    private fun sortValues(): MutableList<Workout> {
        when (workoutSortType) {
            SORTTYPE.DESC -> return workoutHistories
            SORTTYPE.ASC -> {
                var data = mutableListOf<Workout>()
                workoutHistories.forEach {
                    data.add(0, it)
                }
                return data
            }
        }

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
