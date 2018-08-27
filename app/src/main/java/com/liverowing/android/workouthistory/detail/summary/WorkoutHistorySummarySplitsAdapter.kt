package com.liverowing.android.workouthistory.detail.summary

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.liverowing.android.R
import com.liverowing.android.extensions.inflate
import com.liverowing.android.model.pm.SplitTitle
import com.liverowing.android.model.pm.SplitType
import kotlinx.android.synthetic.main.fragment_workout_history_detail_summary_splits_item.view.*
import kotlinx.android.synthetic.main.fragment_workout_history_detail_summary_splits_item_title.view.*

class WorkoutHistorySummarySplitsAdapter(private val items: List<MutableMap<SplitType, Number>>, private val titles: List<SplitTitle>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_TITLE = 0
        const val TYPE_COLUMN = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder = when (viewType) {
            TYPE_TITLE -> TitleViewHolder(parent.inflate(R.layout.fragment_workout_history_detail_summary_splits_item_title))
            else -> ItemViewHolder(parent.inflate(R.layout.fragment_workout_history_detail_summary_splits_item))
        }
        return holder
    }

    override fun getItemViewType(position: Int): Int {
        val type = when (position) {
            0 -> TYPE_TITLE
            else -> TYPE_COLUMN
        }
        return type
    }

    override fun getItemCount(): Int {
        return items.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (position) {
            0 -> (holder as TitleViewHolder).bind(titles)
            else -> (holder as ItemViewHolder).bind(position, items[position - 1], titles)
        }
    }

    class TitleViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(titles: List<SplitTitle>) = with(itemView) {
            f_workout_history_detail_summary_splits_item_title1.text = titles[0].name
            f_workout_history_detail_summary_splits_item_title2.text = titles[1].name
            f_workout_history_detail_summary_splits_item_title3.text = titles[2].name
            f_workout_history_detail_summary_splits_item_title4.text = titles[3].name
        }
    }

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(position: Int, item: MutableMap<SplitType, Number>, titles: List<SplitTitle>) = with(itemView) {
            f_workout_history_detail_summary_splits_item_numbrer.text = position.toString()
            f_workout_history_detail_summary_splits_item_column1.text = item.get(titles[0].type).toString()
            f_workout_history_detail_summary_splits_item_column2.text = item.get(titles[1].type).toString()
            f_workout_history_detail_summary_splits_item_column3.text = item.get(titles[2].type).toString()
            f_workout_history_detail_summary_splits_item_column4.text = item.get(titles[3].type).toString()
        }
    }
}