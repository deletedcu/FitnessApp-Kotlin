package com.liverowing.android.workoutshared.leaderboards


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.LceViewState
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.MvpLceViewStateFragment
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.data.RetainingLceViewState
import com.liverowing.android.LiveRowing
import com.liverowing.android.R
import com.liverowing.android.model.parse.User
import com.liverowing.android.model.parse.UserStats
import com.liverowing.android.model.parse.WorkoutType
import com.liverowing.android.model.parse.WorkoutType.Companion.VALUE_TYPE_TIMED
import com.liverowing.android.util.metric.NumericMetricFormatter
import com.liverowing.android.util.metric.TimeMetricFormatter
import com.parse.ParseUser
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.fragment_workout_leaderboards.*
import timber.log.Timber

class WorkoutLeaderBoardsFragment : MvpLceViewStateFragment<SwipeRefreshLayout, List<UserStats>, WorkoutLeaderBoardsView, WorkoutLeaderBoardsPresenter>(), WorkoutLeaderBoardsView, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var workoutType: WorkoutType
    companion object {
        fun newInstance(workoutType: WorkoutType) : WorkoutLeaderBoardsFragment {
            val fragment = WorkoutLeaderBoardsFragment()
            fragment.workoutType = workoutType
            return fragment
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewDividerItemDecoration: DividerItemDecoration
    private val dataSet = mutableListOf<UserStats>()

    override fun createPresenter() = WorkoutLeaderBoardsPresenter()

    override fun onDestroy() {
        super.onDestroy()
        LiveRowing.refWatcher(this.activity).watch(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workout_leaderboards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val valueFormatter = if (workoutType.valueType == VALUE_TYPE_TIMED) {
            NumericMetricFormatter("%.1fm")
        } else {
            TimeMetricFormatter(true)
        }

        viewManager = LinearLayoutManager(activity!!)
        viewDividerItemDecoration = DividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL)
        viewAdapter = WorkoutLeaderBoardsAdapter(dataSet, valueFormatter, Glide.with(activity!!)) { _, workout ->
            Timber.d("Clicked a userstats row: %s", workout)
        }

        contentView.setOnRefreshListener(this@WorkoutLeaderBoardsFragment)
        recyclerView = f_workout_browser_detail_leaders_and_stats_recyclerview.apply {
            setHasFixedSize(true)
            addItemDecoration(viewDividerItemDecoration)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun loadData(pullToRefresh: Boolean) {
        presenter.loadUserStats(pullToRefresh, workoutType, ParseUser.getCurrentUser() as User)
    }

    override fun onRefresh() {
        loadData(true)
    }

    override fun setData(data: List<UserStats>) {
        dataSet.clear()
        dataSet.addAll(data)
        viewAdapter.notifyDataSetChanged()
    }

    override fun showContent() {
        super.showContent()
        contentView.isRefreshing = false
        if (dataSet.size == 0) {
            emptyView.visibility = View.VISIBLE
        } else {
            emptyView.visibility = View.GONE
        }
    }

    override fun showError(e: Throwable?, pullToRefresh: Boolean) {
        super.showError(e, pullToRefresh)
        contentView.isRefreshing = false
    }

    override fun showLoading(pullToRefresh: Boolean) {
        super.showLoading(pullToRefresh)
        contentView.isRefreshing = pullToRefresh
    }

    override fun createViewState(): LceViewState<List<UserStats>, WorkoutLeaderBoardsView> {
        return RetainingLceViewState<List<UserStats>, WorkoutLeaderBoardsView>()
    }

    override fun getData(): List<UserStats> {
        return dataSet.toList()
    }

    override fun getErrorMessage(e: Throwable?, pullToRefresh: Boolean): String {
        return "There was an error loading:\n\n${e?.message}"
    }
}
