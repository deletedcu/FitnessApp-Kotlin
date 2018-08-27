package com.liverowing.android.workouthistory.detail.summary

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.liverowing.android.R
import com.liverowing.android.extensions.inflate
import com.liverowing.android.model.parse.Workout
import kotlinx.android.synthetic.main.fragment_workout_history_detail_summary_item.view.*

class WorkoutHistorySummaryAdapter(private var items: MutableList<Workout.SummaryItem>): RecyclerView.Adapter<WorkoutHistorySummaryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(R.layout.fragment_workout_history_detail_summary_item))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(item: Workout.SummaryItem) = with(itemView) {
            f_workout_history_detail_summary_item_name.text = item.key
            f_workout_history_detail_summary_item_value.text = item.value
        }
    }

}