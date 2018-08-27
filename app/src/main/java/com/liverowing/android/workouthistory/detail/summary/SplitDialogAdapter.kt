package com.liverowing.android.workouthistory.detail.summary

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.liverowing.android.R
import com.liverowing.android.extensions.inflate
import com.liverowing.android.model.pm.SplitTitle
import kotlinx.android.synthetic.main.dialog_split_item.view.*

class SplitDialogAdapter(private var items: List<SplitTitle>, private var selectedItems: List<SplitTitle>): RecyclerView.Adapter<SplitDialogAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(R.layout.dialog_split_item))

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        var isChecked = false
        selectedItems.forEach {
            if (item.type == it.type) {
                isChecked = true
                return
            }
        }
        holder.bind(item, isChecked)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(item: SplitTitle, isChecked: Boolean) = with(itemView) {
            split_checkBox.text = item.name
            split_checkBox.isChecked = isChecked
        }
    }
}