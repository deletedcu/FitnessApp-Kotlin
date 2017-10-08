package com.liverowing.liverowing.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.liverowing.liverowing.R
import com.liverowing.liverowing.api.model.WorkoutType
import com.liverowing.liverowing.inflate
import com.liverowing.liverowing.loadUrl
import com.liverowing.liverowing.util.PicassoCircleTransformation
import kotlinx.android.synthetic.main.fragment_dashboard_workout_type_card.view.*

/**
 * Created by henrikmalmberg on 2017-10-08.
 */
class DashboardWorkoutTypeAdapter(val items: List<WorkoutType>, val listener: (WorkoutType) -> Unit) : RecyclerView.Adapter<DashboardWorkoutTypeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.fragment_dashboard_workout_type_card))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], listener)

    override fun getItemCount() = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: WorkoutType, listener: (WorkoutType) -> Unit) = with(itemView) {
            if (item.image !== null) f_dashboard_workouttype_card_image.loadUrl(item.image!!.url)
            if (item.createdBy !== null && item.createdBy!!.image !== null) f_dashboard_workouttype_card_createdby_image.loadUrl(item.createdBy!!.image!!.url, PicassoCircleTransformation())
            f_dashboard_workouttype_card_workouttype_name.text = item.name
            f_dashboard_workouttype_card_createdby_name.text = "By | " + item.createdBy!!.displayName
            setOnClickListener { listener(item) }
        }
    }
}

