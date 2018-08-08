package com.liverowing.android.workoutbrowser

import android.os.Bundle
import android.view.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.LceViewState
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.MvpLceViewStateFragment
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.data.RetainingLceViewState
import com.liverowing.android.LiveRowing
import com.liverowing.android.R
import com.liverowing.android.extensions.dpToPx
import com.liverowing.android.model.parse.WorkoutType
import com.liverowing.android.util.GridSpanDecoration
import kotlinx.android.synthetic.main.fragment_workout_browser.*
import org.greenrobot.eventbus.EventBus
import timber.log.Timber


class WorkoutBrowserFragment : MvpLceViewStateFragment<SwipeRefreshLayout, List<WorkoutType>, WorkoutBrowserView, WorkoutBrowserPresenter>(), WorkoutBrowserView, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: GridLayoutManager
    private lateinit var viewDividerItemDecoration: GridSpanDecoration

    private var dataSet = mutableListOf<WorkoutType>()

    override fun createPresenter() = WorkoutBrowserPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("** onCreate")
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
    }

    override fun onDestroy() {
        Timber.d("** onDestroy")
        super.onDestroy()
        LiveRowing.refWatcher(this.activity).watch(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("** onCreateView")
        return inflater.inflate(R.layout.fragment_workout_browser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("** onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        viewManager = GridLayoutManager(activity!!, 2)
        viewDividerItemDecoration = GridSpanDecoration(8.dpToPx())
        viewAdapter = WorkoutBrowserAdapter(dataSet, Glide.with(this), onClick = { _, workout ->
            EventBus.getDefault().postSticky(workout)
            view.findNavController().navigate(R.id.action_workoutBrowserFragment_to_workoutBrowserDetailActivity)
        })

        contentView.setOnRefreshListener(this@WorkoutBrowserFragment)
        recyclerView = f_workout_browser_recyclerview.apply {
            setHasFixedSize(true)
            addItemDecoration(viewDividerItemDecoration)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.workout_browser, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
        }

        return super.onOptionsItemSelected(item)
    }

    override fun loadData(pullToRefresh: Boolean) {
        Timber.d("** loadData")
        presenter.loadWorkoutTypes(pullToRefresh)
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

    override fun createViewState(): LceViewState<List<WorkoutType>, WorkoutBrowserView> = RetainingLceViewState()

    override fun setData(data: List<WorkoutType>) {
        Timber.d("** setData")
        dataSet.clear()
        dataSet.addAll(data)
        viewAdapter.notifyDataSetChanged()
    }

    override fun getData(): List<WorkoutType> {
        Timber.d("** getData")
        return dataSet.toList()
    }

    override fun getErrorMessage(e: Throwable?, pullToRefresh: Boolean): String {
        Timber.d("** getErrorMessage")
        return e?.message!!
    }

    override fun onRefresh() {
        Timber.d("** onRefresh")
        loadData(true)
    }

}
