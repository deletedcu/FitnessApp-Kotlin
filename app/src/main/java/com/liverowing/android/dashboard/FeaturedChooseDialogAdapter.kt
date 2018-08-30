package com.liverowing.android.dashboard

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.liverowing.android.R
import com.liverowing.android.extensions.inflate
import com.liverowing.android.model.parse.User
import kotlinx.android.synthetic.main.dialog_choose_featured_item.view.*

class FeaturedChooseDialogAdapter(private val items: MutableList<User>, private val checkedItems: MutableList<User>, private val glide: RequestManager, private val onUpdatedItems: (MutableList<User>) -> Unit): RecyclerView.Adapter<FeaturedChooseDialogAdapter.ViewHolder>() {

    private val selectedItems = mutableListOf<User>()

    init {
        selectedItems.addAll(checkedItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(R.layout.dialog_choose_featured_item), glide)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isChecked = selectedItems.map { item -> item.objectId }.contains(items[position].objectId)
        holder.bind(items[position], isChecked, onCheckChanged = { item, isChecked ->
            if (isChecked) {
                selectedItems.add(item)
            } else {
                selectedItems.remove(item)
            }
            onUpdatedItems(selectedItems)
        })
    }

    class ViewHolder(view: View, private val glide: RequestManager): RecyclerView.ViewHolder(view) {
        fun bind(item: User, isChecked: Boolean, onCheckChanged: (item: User, isChecked: Boolean) -> Unit) = with(itemView) {
            glide
                    .load(item.image?.url)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .into(dialog_choose_featured_item_image)
            dialog_choose_featured_item_name.text = item.username
            dialog_choose_featured_item_name.isChecked = isChecked
            dialog_choose_featured_item_name.setOnCheckedChangeListener { _, isChecked ->
                onCheckChanged(item, isChecked)
            }
        }
    }
}