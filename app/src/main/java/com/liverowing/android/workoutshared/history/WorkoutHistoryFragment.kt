package com.liverowing.android.workoutshared.history


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.LceViewState
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.MvpLceViewStateFragment
import com.liverowing.android.LiveRowing
import com.liverowing.android.R
import com.liverowing.android.model.parse.Workout
import com.liverowing.android.model.parse.WorkoutType
import org.greenrobot.eventbus.EventBus

class WorkoutHistoryFragment : MvpLceViewStateFragment<SwipeRefreshLayout, List<Workout>, WorkoutHistoryView, WorkoutHistoryPresenter>() {
    private lateinit var workoutType: WorkoutType
    companion object {
        fun newInstance(workoutType: WorkoutType) : WorkoutHistoryFragment {
            val fragment = WorkoutHistoryFragment()
            fragment.workoutType = workoutType
            return fragment
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LiveRowing.refWatcher(this.activity).watch(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workout_browser_detail_workout_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val workoutType = EventBus.getDefault().getStickyEvent(WorkoutType::class.java)

    }

    override fun loadData(pullToRefresh: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createPresenter(): WorkoutHistoryPresenter {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createViewState(): LceViewState<List<Workout>, WorkoutHistoryView> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setData(data: List<Workout>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getData(): List<Workout> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getErrorMessage(e: Throwable?, pullToRefresh: Boolean): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
