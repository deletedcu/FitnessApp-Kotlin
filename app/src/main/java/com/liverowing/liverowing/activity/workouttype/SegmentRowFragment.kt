package com.liverowing.liverowing.activity.workouttype


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liverowing.liverowing.R

class SegmentRowFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_segment_row, container, false)
    }

    companion object {
        fun newInstance(): SegmentRowFragment {
            return SegmentRowFragment()
        }
    }
}
