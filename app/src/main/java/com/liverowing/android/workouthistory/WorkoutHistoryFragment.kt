package com.liverowing.android.workouthistory

import android.os.Bundle
import android.view.*
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.LceViewState
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.MvpLceViewStateFragment
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.data.RetainingLceViewState
import com.liverowing.android.LiveRowing
import com.liverowing.android.MainActivity
import com.liverowing.android.R
import com.liverowing.android.R.id.*
import com.liverowing.android.model.parse.Workout
import com.liverowing.android.workouthistory.bottomSheet.BottomSheetFragment
import com.liverowing.android.workouthistory.bottomSheet.BottomSheetListener
import kotlinx.android.synthetic.main.fragment_workout_history.*
import org.greenrobot.eventbus.EventBus

class WorkoutHistoryFragment : MvpLceViewStateFragment<SwipeRefreshLayout, List<Workout>, WorkoutHistoryView, WorkoutHistoryPresenter>(), WorkoutHistoryView, SwipeRefreshLayout.OnRefreshListener, BottomSheetListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewDividerItemDecoration: DividerItemDecoration

    private var dataSet = mutableListOf<Workout>()
    private val workoutTypesFilter = mutableSetOf<Int>()
    private val workoutTypesMap = hashMapOf(
            action_single_distance to 1,
            action_single_time to 2,
            action_intervals to 4
    )

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
            findNavController(view).navigate(R.id.workoutHistoryDetailAction)
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

                presenter.loadWorkouts(false, workoutTypesFilter)
            }

            else -> {}
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showBottomMenu(workout: Workout) {
        val bottomSheetFragment = BottomSheetFragment.newInstance(workout, this)
        bottomSheetFragment.show(fragmentManager, bottomSheetFragment.javaClass.toString())
    }

    override fun loadData(pullToRefresh: Boolean) {
        presenter.loadWorkouts(pullToRefresh, workoutTypesFilter)
    }

    override fun onRefresh() {
        loadData(true)
    }

    override fun setData(data: List<Workout>) {
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
