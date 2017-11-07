package com.liverowing.liverowing.activity.dashboard


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.liverowing.liverowing.R


/**
 * A simple [Fragment] subclass.
 */
class DashboardWorkoutTypeCard : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard_workout_type_card, container, false)
    }

}// Required empty public constructor
