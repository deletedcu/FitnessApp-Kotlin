package com.liverowing.android.workoutshared.workoutdetails

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.liverowing.android.R
import com.liverowing.android.extensions.inflate
import com.liverowing.android.extensions.secondsToTimespan
import com.liverowing.android.model.parse.Segment
import com.liverowing.android.model.parse.SegmentValueType
import kotlinx.android.synthetic.main.fragment_workout_segment_item.view.*

class WorkoutSegmentAdapter(private val items: List<Segment>, private val glide: RequestManager, private val onClick: (View, Segment) -> Unit) : RecyclerView.Adapter<WorkoutSegmentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.fragment_workout_segment_item), glide)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.bind(items[position], onClick) }
    override fun getItemCount() = items.size

    class ViewHolder(view: View, private val glide: RequestManager) : RecyclerView.ViewHolder(view) {
        fun bind(item: Segment, onClick: (View, Segment) -> Unit) = with(itemView) {
            glide
                    .load(item.image?.url)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
                    .into(f_workout_segment_image)

            val segmentDetails = StringBuilder()
            segmentDetails.append(if (item.valueType == SegmentValueType.TIMED.value) "Duration: " else "Distance: ")
            segmentDetails.append(item.friendlyValue + "\n")
            if (item.targetRate != null) segmentDetails.append("Target Rate: " + item.targetRate + "\n")
            if (item.restValue != null) segmentDetails.append("Rest Period: " + item.restValue?.secondsToTimespan(false) + "\n")

            f_workout_segment_title.text = if (item.name.isNullOrEmpty()) "Interval " + (adapterPosition + 1) else item.name
            f_workout_segment_details.text = segmentDetails

            setOnClickListener { onClick(itemView, item) }
        }
    }
}