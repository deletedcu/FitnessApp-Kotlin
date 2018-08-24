package com.liverowing.android.workouthistory.detail.summary


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.liverowing.android.LiveRowing
import com.liverowing.android.R
import com.liverowing.android.model.parse.Workout
import com.liverowing.android.model.parse.WorkoutType
import kotlinx.android.synthetic.main.fragment_workout_history_detail_summary.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat

class WorkoutHistorySummaryFragment : Fragment() {

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

        f_workout_history_detail_name.text = workoutType?.createdBy?.userClass
        f_workout_history_detail_createdat.text = SimpleDateFormat.getDateInstance().format(workout.createdAt)

    }
}
