package com.liverowing.android.activity.workouttype

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.liverowing.android.R
import com.liverowing.android.adapter.SegmentAdapter
import com.liverowing.android.extensions.toggle
import com.liverowing.android.model.parse.Segment
import com.liverowing.android.model.parse.WorkoutType
import kotlinx.android.synthetic.main.fragment_workout_type_details.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.animation.ObjectAnimator
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.EventBus

class WorkoutTypeDetailsFragment : Fragment() {
    private lateinit var workoutType: WorkoutType
    private var segments = mutableListOf<Segment>()

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_workout_type_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        f_workout_type_details_interval_recyclerview.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = SegmentAdapter(segments, Glide.with(this), { image, segment ->
                run {
                    Log.d("LiveRowing", "Clicked Segment row")
                }
            })
        }

        f_workout_type_details_interval_toggle.isClickable = false
        f_workout_type_details_interval_group.setOnClickListener {
            f_workout_type_details_interval_friendly.toggle()
            f_workout_type_details_interval_recyclerview.toggle()

            val rotation = f_workout_type_details_interval_toggle.rotationX
            val animation = ObjectAnimator.ofFloat(f_workout_type_details_interval_toggle, "rotationX", rotation, rotation + 180f)
            animation.duration = 250
            animation.interpolator = AccelerateDecelerateInterpolator()
            animation.start()
        }
    }

    @Subscribe(sticky = true)
    fun onReceiveWorkoutType(workoutType: WorkoutType) {
        this.workoutType = workoutType
        f_workout_type_details_createdby_username.text = workoutType.createdBy!!.username
        f_workout_type_details_description_text.text = workoutType.descriptionText.toString()

        val friendlySegmentDescription = workoutType.friendlySegmentDescription
        if (!friendlySegmentDescription.isNullOrEmpty()) {
            f_workout_type_details_interval_group.visibility = View.VISIBLE
            f_workout_type_details_interval_friendly.text = friendlySegmentDescription

            segments.clear()
            segments.addAll(workoutType.segments!!)
            f_workout_type_details_interval_recyclerview.adapter.notifyDataSetChanged()
        } else {
            f_workout_type_details_interval_group.visibility = View.GONE
        }

        if (workoutType.createdBy!!.image != null && workoutType.createdBy!!.image?.url != null) {
            Glide.with(activity)
                    .load(workoutType.createdBy?.image?.url)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(f_workout_type_details_createdby_image)
        }
    }
}
