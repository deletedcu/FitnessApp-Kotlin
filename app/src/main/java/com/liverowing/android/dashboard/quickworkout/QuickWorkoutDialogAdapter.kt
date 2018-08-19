package com.liverowing.android.dashboard.quickworkout

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.liverowing.android.R
import com.liverowing.android.extensions.inflate
import kotlinx.android.synthetic.main.fragment_quick_workout_item.view.*

class QuickWorkoutDialogAdapter(private val items: List<QuickWorkoutDialogPresenter.QuickWorkoutItem>, private val glide: RequestManager, private val onClick: (View, QuickWorkoutDialogPresenter.QuickWorkoutItem) -> Unit) : RecyclerView.Adapter<QuickWorkoutDialogAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.fragment_quick_workout_item), glide)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onClick)
    }

    override fun getItemCount() = items.size

    class ViewHolder(view: View, private val glide: RequestManager) : RecyclerView.ViewHolder(view) {
        fun bind(item: QuickWorkoutDialogPresenter.QuickWorkoutItem, onClick: (View, QuickWorkoutDialogPresenter.QuickWorkoutItem) -> Unit) = with(itemView) {
            f_quick_workout_item_name.text = item.name
            f_quick_workout_item_name.setCompoundDrawables(null, null, null, null)
            if (item.type == "group") {
                f_quick_workout_item_name.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right, 0)
            }

            if (item.icon != null) {
                if (item.icon.startsWith("http")) {
                    glide
                            .load(item.icon)
                            .apply( RequestOptions.bitmapTransform(CircleCrop()))
                            .into(f_quick_workout_item_icon)
                } else {
                    val resourceId = context.resources.getIdentifier(item.icon, "drawable", context.packageName)
                    if (resourceId > 0) {
                        f_quick_workout_item_icon.setImageResource(resourceId)
                    }
                }
            }
            setOnClickListener { onClick(itemView, item) }
        }
    }
}