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
class DashboardWorkoutTypeAdapter(val items: List<WorkoutType>, val width: Int?, val height: Int?, val listener: (View, WorkoutType) -> Unit) : RecyclerView.Adapter<DashboardWorkoutTypeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.fragment_dashboard_workout_type_card), width, height)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position], listener)

    override fun getItemCount() = items.size

    class ViewHolder(itemView: View, width: Int?, height: Int?) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: WorkoutType, listener: (View, WorkoutType) -> Unit) = with(itemView) {
            if (item.image !== null) f_dashboard_workouttype_card_image.loadUrl(item.image!!.url)
            if (item.createdBy !== null && item.createdBy!!.image !== null) f_dashboard_workouttype_card_createdby_image.loadUrl(item.createdBy!!.image!!.url, PicassoCircleTransformation())
            f_dashboard_workouttype_card_workouttype_name.text = item.name
            f_dashboard_workouttype_card_createdby_name.text = "By | " + (item.createdBy?.displayName ?: item.createdBy?.username)
            setOnClickListener { listener(f_dashboard_workouttype_card_image, item) }
        }

        init {
            val layoutParams = itemView.layoutParams

            if (width != null) {
                layoutParams.width = width
                if (width <= 300) {
                    itemView.f_dashboard_workouttype_card_createdby_image.layoutParams.width = 1
                    itemView.f_dashboard_workouttype_card_createdby_image.visibility = View.INVISIBLE
                }
            }
            if (height != null) layoutParams.height = height
        }
    }
}

