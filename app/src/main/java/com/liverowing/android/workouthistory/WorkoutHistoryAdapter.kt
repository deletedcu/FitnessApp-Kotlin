package com.liverowing.android.workouthistory

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.liverowing.android.R
import com.liverowing.android.extensions.inflate
import com.liverowing.android.extensions.secondsToTimespan
import com.liverowing.android.model.parse.Workout
import kotlinx.android.synthetic.main.fragment_workout_history_item.view.*
import java.text.SimpleDateFormat

class WorkoutHistoryAdapter(private val items: List<Workout>, private val glide: RequestManager, private val onClick: (View, Workout) -> Unit, private val onOptionsClick: (View, Workout) -> Unit) : RecyclerView.Adapter<WorkoutHistoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.fragment_workout_history_item), glide)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.bind(items[position], onClick, onOptionsClick) }
    override fun getItemCount() = items.size

    class ViewHolder(view: View, private val glide: RequestManager) : RecyclerView.ViewHolder(view) {
        fun bind(item: Workout, onClick: (View, Workout) -> Unit, onOptionsClick: (View, Workout) -> Unit) = with(itemView) {

            glide
                    .load(item.workoutType?.createdBy?.image?.url)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(8)))
                    .into(f_workout_history_item_image)

            f_workout_history_item_title.text = item.workoutType?.name
            f_workout_history_item_metrics.text = String.format("Pace: %s | Rate: %d | Time: %s", item.averageSplitTime?.secondsToTimespan(), item.averageSPM, item.totalTime?.secondsToTimespan())
            f_workout_history_item_date.text = SimpleDateFormat.getDateTimeInstance().format(item.createdAt)

            setOnClickListener { onClick(itemView, item) }
            f_workout_history_item_options.setOnClickListener { onOptionsClick(itemView, item) }
        }
    }
}