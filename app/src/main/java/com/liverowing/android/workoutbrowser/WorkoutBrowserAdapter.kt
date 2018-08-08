package com.liverowing.android.workoutbrowser

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.liverowing.android.R
import com.liverowing.android.extensions.inflate
import com.liverowing.android.model.parse.WorkoutType
import kotlinx.android.synthetic.main.fragment_workout_card_item.view.*

class WorkoutBrowserAdapter(private val items: List<WorkoutType>, private val glide: RequestManager, private val onClick: (View, WorkoutType) -> Unit) : RecyclerView.Adapter<WorkoutBrowserAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.fragment_workout_card_item), glide)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) { holder.bind(items[position], onClick) }
    override fun getItemCount() = items.size

    class ViewHolder(view: View, private val glide: RequestManager) : RecyclerView.ViewHolder(view) {
        fun bind(item: WorkoutType, onClick: (View, WorkoutType) -> Unit) = with(itemView) {
            glide
                    .load(item.image?.url)
                    .into(fragment_workout_card_item_image)

            glide
                    .load(item.createdBy?.image?.url)
                    .apply( RequestOptions.bitmapTransform(CircleCrop()))
                    .into(fragment_workout_card_item_createdby_image)

            fragment_workout_card_item_name.text = item.name
            fragment_workout_card_item_createdby.text = item.createdBy?.username

            setOnClickListener { onClick(itemView, item) }
        }
    }
}