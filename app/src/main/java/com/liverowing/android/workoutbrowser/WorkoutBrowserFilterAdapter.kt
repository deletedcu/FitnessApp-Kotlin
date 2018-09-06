package com.liverowing.android.workoutbrowser

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.liverowing.android.R
import com.liverowing.android.extensions.inflate
import com.liverowing.android.model.pm.FilterItem
import kotlinx.android.synthetic.main.workout_browser_backdrop_item.view.*

class WorkoutBrowserFilterAdapter(
        private val items: List<FilterItem>,
        private var selectedItems: MutableList<FilterItem>,
        private val isMultipleSelected: Boolean,
        private val onSelectChanged: (selectedItems: MutableList<FilterItem>) -> Unit) : RecyclerView.Adapter<WorkoutBrowserFilterAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(R.layout.workout_browser_backdrop_item))

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        var isSelected = false
        selectedItems.forEach {
            if (it.key == item.key) {
                isSelected = true
                return@forEach
            }
        }

        holder.bind(item, isSelected, onToggle = { item, isSelected ->
            if (isSelected) {
                if (isMultipleSelected) {
                    selectedItems.add(item)
                } else {
                    selectedItems.clear()
                    selectedItems.add(item)
                }
            } else {
                if (isMultipleSelected) {
                    selectedItems.remove(item)
                }
            }
            notifyDataSetChanged()
            onSelectChanged(selectedItems)
        })
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(item: FilterItem, isSelected: Boolean, onToggle: (item: FilterItem, isSelected: Boolean) -> Unit) = with(itemView) {
            workout_browser_backdrop_item_button.text = item.value
            workout_browser_backdrop_item_button.isSelected = isSelected
            workout_browser_backdrop_item_button.setOnClickListener {
                onToggle(item, !isSelected)
            }
        }
    }
}