package com.liverowing.liverowing.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.RequestOptions
import com.liverowing.liverowing.R
import com.liverowing.liverowing.model.parse.UserStats
import com.liverowing.liverowing.inflate
import com.liverowing.liverowing.secondsToTimespan
import kotlinx.android.synthetic.main.fragment_user_stats_row.view.*


/**
 * Created by henrikmalmberg on 2017-11-02.
 */
class UserStatsAdapter(private val items: List<UserStats>, val glide: RequestManager, private val listener: (View, UserStats) -> Unit) : RecyclerView.Adapter<UserStatsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.fragment_user_stats_row), glide)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], listener)

    override fun getItemCount() = items.size

    class ViewHolder(itemView: View, val glide: RequestManager) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: UserStats, listener: (View, UserStats) -> Unit) = with(itemView) {
            if (item.user?.image != null) {
                glide
                        .load(item.user?.image?.url)
                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .into(f_user_stats_row_image)
            }
            if (item.user?.userClass != null) f_user_stats_row_username.text = item.user!!.username
            f_user_stats_row_value.text = item.record.value.secondsToTimespan(true)
        }
    }
}

