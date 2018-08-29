package com.liverowing.android.workouthistory

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.liverowing.android.R
import com.liverowing.android.extensions.inflate
import com.liverowing.android.model.parse.WorkoutType
import kotlinx.android.synthetic.main.fragment_dashboard_featured_card_item.view.*
import kotlinx.android.synthetic.main.fragment_dashboard_workout_card_item.view.*

enum class CardType {
    TYPE_FEATURED, TYPE_WORKOUT
}

class DashboardAdapter(val cardType: CardType, private val items: List<WorkoutType>, private val glide: RequestManager, private val onClick: (View, WorkoutType) -> Unit, private val onMoreClick: (View, WorkoutType) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (cardType) {
            CardType.TYPE_FEATURED -> FeaturedViewHolder(parent.inflate(R.layout.fragment_dashboard_featured_card_item), glide)
            else -> WorkoutViewHolder(parent.inflate(R.layout.fragment_dashboard_workout_card_item), glide)
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (cardType) {
            CardType.TYPE_FEATURED -> (holder as FeaturedViewHolder).bind(items[position], onClick, onMoreClick)
            else -> (holder as WorkoutViewHolder).bind(items[position], onClick)
        }
    }
    override fun getItemCount() = items.size

    class FeaturedViewHolder(view: View, private val glide: RequestManager) : RecyclerView.ViewHolder(view) {
        fun bind(item: WorkoutType, onClick: (View, WorkoutType) -> Unit, onMoreClick: (View, WorkoutType) -> Unit) = with(itemView) {
            glide
                    .load(item.image?.url)
                    .apply(RequestOptions.placeholderOf(R.drawable.side_nav_bar))
                    .into(fragment_dashboard_featured_card_item_image)

            fragment_dashboard_featured_card_item_name.text = item.name
            fragment_dashboard_featured_card_item_createdby.text = item.createdBy?.username

            setOnClickListener { onClick(itemView, item) }
            fragment_dashboard_featured_card_item_more.setOnClickListener { onMoreClick(itemView, item) }
        }
    }

    class WorkoutViewHolder(view: View, private val glide: RequestManager) : RecyclerView.ViewHolder(view) {
        fun bind(item: WorkoutType, onClick: (View, WorkoutType) -> Unit) = with(itemView) {
            glide
                    .load(item.image?.url)
                    .apply(RequestOptions.placeholderOf(R.drawable.side_nav_bar))
                    .into(fragment_dashboard_workout_card_item_image)

            fragment_dashboard_workout_card_item_name.text = item.name
            fragment_dashboard_workout_card_item_createdby.text = item.createdBy?.username

            setOnClickListener { onClick(itemView, item) }
        }
    }
}