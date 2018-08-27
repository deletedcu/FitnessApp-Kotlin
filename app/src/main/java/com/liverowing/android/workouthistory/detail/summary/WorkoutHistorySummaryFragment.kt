package com.liverowing.android.workouthistory.detail.summary


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.JustifyContent
import com.liverowing.android.LiveRowing
import com.liverowing.android.R
import com.liverowing.android.extensions.dpToPx
import com.liverowing.android.model.parse.Workout
import com.liverowing.android.model.pm.SplitTitle
import com.liverowing.android.model.pm.SplitType
import com.liverowing.android.views.LiveRowingFlexboxLayoutManager
import kotlinx.android.synthetic.main.fragment_workout_history_detail_summary.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat

class WorkoutHistorySummaryFragment : Fragment() {

    private lateinit var summaryRecyclerView: RecyclerView
    private lateinit var summaryAdapter: WorkoutHistorySummaryAdapter
    private lateinit var summaryViewManager: LiveRowingFlexboxLayoutManager
    private var summaryDataset = mutableListOf<Workout.SummaryItem>()
    private var isSummaryCollapsed = true

    private lateinit var splitRecyclerView: RecyclerView
    private lateinit var splitViewAdapter: RecyclerView.Adapter<*>
    private lateinit var splitViewManager: RecyclerView.LayoutManager
    private var splitTitles = SplitTitle.defaultData()
    private var splitDataSet = mutableListOf<MutableMap<SplitType, String>>()

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this@WorkoutHistorySummaryFragment)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this@WorkoutHistorySummaryFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        LiveRowing.refWatcher(this.activity).watch(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workout_history_detail_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onWorkoutTypeMainThread(workout: Workout) {
        val workoutType = workout.workoutType
        Glide
                .with(activity!!)
                .load(workoutType?.image?.url)
                .apply(RequestOptions.placeholderOf(R.drawable.side_nav_bar))
                .into(f_workout_history_detail_image)

        Glide
                .with(activity!!)
                .load(workoutType?.createdBy?.image?.url)
                .apply(RequestOptions.bitmapTransform(CircleCrop()).placeholder(R.drawable.ic_launcher_background))
                .into(f_workout_history_detail_createdby_image)

        f_workout_history_detail_name.text = workoutType?.createdBy?.username
        f_workout_history_detail_createdat.text = SimpleDateFormat.getDateTimeInstance().format(workout.createdAt)

        summaryDataset.clear()
        if (workout.data.WorkoutData.getData() is List<Workout.SummaryItem>) {
            summaryDataset.addAll(workout.data.WorkoutData.getData())
            summaryAdapter.notifyDataSetChanged()
        }

        splitDataSet.clear()
        if (workout.data.WorkoutData.splits is List<Workout.Split>) {
            val splits = workout.data.WorkoutData.splits
            splits.forEach {
                splitDataSet.add(it.getMap())
            }
            splitViewAdapter.notifyDataSetChanged()
        }
    }

    private fun setupUI() {
        summaryViewManager = LiveRowingFlexboxLayoutManager(context!!)
        summaryViewManager.isScrollEnabled = false
        summaryViewManager.flexDirection = FlexDirection.ROW
        summaryViewManager.justifyContent = JustifyContent.FLEX_START
        summaryViewManager.flexWrap = FlexWrap.WRAP
        summaryAdapter = WorkoutHistorySummaryAdapter(summaryDataset)
        summaryRecyclerView = f_workout_history_detail_summary_recyclerview.apply {
            layoutManager = summaryViewManager
            adapter = summaryAdapter
        }

        splitViewManager = LinearLayoutManager(activity!!)
        splitViewAdapter = WorkoutHistorySummarySplitsAdapter(splitDataSet, splitTitles)

        splitRecyclerView = f_workout_history_detail_summary_splits_recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = splitViewManager
            adapter = splitViewAdapter
        }

        f_workout_history_detail_summary_more.setOnClickListener {
            showSplitDialog()
        }

        f_workout_history_detail_summary_collapse_button.setOnClickListener {
            if (isSummaryCollapsed) {
                isSummaryCollapsed = false
                f_workout_history_detail_summary_collapse_button.setImageResource(R.drawable.ic_arrow_up)
                var layoutparams = f_workout_history_detail_summary_layout.layoutParams
                layoutparams.width = ViewGroup.LayoutParams.MATCH_PARENT
                layoutparams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                f_workout_history_detail_summary_layout.layoutParams = layoutparams
            } else {
                isSummaryCollapsed = true
                f_workout_history_detail_summary_collapse_button.setImageResource(R.drawable.ic_arrow_down)
                var layoutparams = f_workout_history_detail_summary_layout.layoutParams
                layoutparams.width = ViewGroup.LayoutParams.MATCH_PARENT
                layoutparams.height = 75.dpToPx()
                f_workout_history_detail_summary_layout.layoutParams = layoutparams
            }
        }
    }

    private fun showSplitDialog() {
        val dialogFragment = SplitDialogFragment(splitTitles, onSelectedItems = { selectedList ->
            splitTitles = selectedList
            splitViewAdapter.notifyDataSetChanged()
        })
        dialogFragment.show(fragmentManager, dialogFragment.javaClass.toString())
    }

}
