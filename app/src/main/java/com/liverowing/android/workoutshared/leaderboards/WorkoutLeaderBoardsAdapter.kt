package com.liverowing.android.workoutshared.leaderboards

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.liverowing.android.R
import com.liverowing.android.extensions.inflate
import com.liverowing.android.model.parse.UserStats
import kotlinx.android.synthetic.main.fragment_workout_browser_detail_leaders_and_stats_item.view.*

class WorkoutLeaderBoardsAdapter(private val items: List<UserStats>, private val glide: RequestManager, private val onClick: (View, UserStats) -> Unit) : RecyclerView.Adapter<WorkoutLeaderBoardsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.fragment_workout_browser_detail_leaders_and_stats_item), glide)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.bind(position, items[position], onClick) }
    override fun getItemCount() = items.size

    class ViewHolder(view: View, private val glide: RequestManager) : RecyclerView.ViewHolder(view) {
        fun bind(position: Int, item: UserStats, onClick: (View, UserStats) -> Unit) = with(itemView) {

            glide
                    .load(item.user?.image?.url)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_item_placeholder))
                    .into(f_workout_browser_detail_leaders_and_stats_item_image)

            f_workout_browser_detail_leaders_and_stats_item_number.text = item.record.rank.toString()
            f_workout_browser_detail_leaders_and_stats_item_user.text = item.user?.username
            f_workout_browser_detail_leaders_and_stats_item_metric.text = item.record.value.toString()

            setOnClickListener { onClick(itemView, item) }
        }
    }
}