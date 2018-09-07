package com.liverowing.android.workoutbrowser

import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.liverowing.android.R
import com.liverowing.android.extensions.inflate
import com.liverowing.android.model.parse.WorkoutType
import com.parse.ParseObject
import kotlinx.android.synthetic.main.fragment_workout_card_item.view.*

class WorkoutBrowserAdapter(
        private val items: List<ParseObject>,
        private val glide: RequestManager,
        private val onClick: (View, WorkoutType) -> Unit,
        private val onMoreClick: (WorkoutType) -> Unit) : RecyclerView.Adapter<WorkoutBrowserAdapter.ViewHolder>() {
    val spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return if (position % 3 == 2) 2 else 1
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is WorkoutType) {
            R.layout.fragment_workout_card_item
        } else {
            R.layout.fragment_workout_browser_header_item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == R.layout.fragment_workout_card_item) {
        }

        return ViewHolder(parent.inflate(viewType), glide)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isSingleCard = (position % 3 == 2)
        holder.bind(items[position], isSingleCard, onClick, onMoreClick)
    }

    override fun getItemCount() = items.size

    class ViewHolder(view: View, private val glide: RequestManager) : RecyclerView.ViewHolder(view) {
        fun bind(item: ParseObject, isSingleCard: Boolean, onClick: (View, WorkoutType) -> Unit, onMoreClick: (WorkoutType) -> Unit) = with(itemView) {
            if (item is WorkoutType) {
                glide
                        .load(item.image?.url)
                        .into(fragment_workout_card_item_image)

                if (isSingleCard) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        fragment_workout_card_item_name.setTextAppearance(R.style.TextAppearanceCustomHeadline6)
                    } else {
                        fragment_workout_card_item_name.setTextAppearance(context!!, R.style.TextAppearanceCustomHeadline6)
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        fragment_workout_card_item_name.setTextAppearance(R.style.TextAppearanceCustomSubtitle2)
                    } else {
                        fragment_workout_card_item_name.setTextAppearance(context!!, R.style.TextAppearanceCustomSubtitle2)
                    }
                }
                fragment_workout_card_item_name.text = item.name
                fragment_workout_card_item_createdby.text = item.createdBy?.username
                fragment_workout_card_item_value.text = item.getValueText()

                fragment_workout_card_item_more.setOnClickListener {
                    onMoreClick(item)
                }

                setOnClickListener { onClick(itemView, item) }
            }
        }
    }
}