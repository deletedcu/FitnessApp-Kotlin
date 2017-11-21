package com.liverowing.liverowing.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.liverowing.liverowing.R
import com.liverowing.liverowing.inflate
import com.liverowing.liverowing.model.parse.Segment
import com.liverowing.liverowing.model.parse.SegmentValueType
import com.liverowing.liverowing.secondsToTimespan
import kotlinx.android.synthetic.main.fragment_segment_row.view.*

class SegmentAdapter(private val items: List<Segment>, val glide: RequestManager, private val listener: (View, Segment) -> Unit) : RecyclerView.Adapter<SegmentAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.fragment_segment_row), glide)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], listener)

    override fun getItemCount() = items.size

    class ViewHolder(itemView: View, val glide: RequestManager) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Segment, listener: (View, Segment) -> Unit) = with(itemView) {
            val intervalDescription = StringBuilder()
            intervalDescription.append(if (item.valueType == SegmentValueType.TIMED.value) "Duration: " else "Distance: ")
            intervalDescription.append(item.friendlyValue + "\n")
            if (item.targetRate != null) intervalDescription.append("Target Rate: " + item.targetRate + "\n")
            if (item.restValue != null) intervalDescription.append("Rest Period: " + item.restValue?.secondsToTimespan(false) + "\n")

            if (item.image != null) {
                glide
                        .load(item.image?.url)
                        .into(f_segment_row_image)
            }

            f_segment_row_title.text = if (item.name.isNullOrEmpty()) "Interval " + (adapterPosition + 1) else item.name
            f_segment_row_metrics.text = intervalDescription
        }
    }
}