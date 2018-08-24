package com.liverowing.android.workouthistory.detail.summary

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.liverowing.android.R
import com.liverowing.android.extensions.inflate
import com.liverowing.android.model.pm.SplitTitle
import com.liverowing.android.model.pm.SplitType

class WorkoutHistorySummarySplitsAdapter(private val items: List<HashMap<SplitType, Number>>, private val titles: List<SplitTitle>): RecyclerView.Adapter<WorkoutHistorySummarySplitsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(R.layout.fragment_workout_history_detail_summary_splits_item))

    override fun getItemCount(): Int {
        return items.size + 1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }


    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(position: Int) {
            when (position) {
                0 -> {

                }
                else -> {

                }
            }
        }
    }
}