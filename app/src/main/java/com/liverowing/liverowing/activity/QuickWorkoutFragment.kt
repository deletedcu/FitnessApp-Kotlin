package com.liverowing.liverowing.activity


import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar

import com.liverowing.liverowing.R
import com.liverowing.liverowing.milliSecondsToTimespan
import kotlinx.android.synthetic.main.fragment_quick_workout.*

class QuickWorkoutFragment : BottomSheetDialogFragment() {
    var mType: Int = 1
    val mTimeArray = listOf(60000, 240000, 1800000, 3600000)
    val mDistanceArray = listOf(100, 500, 1000, 2000, 5000, 6000, 10000)

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_quick_workout, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        f_quick_workout_seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (mType == 1) {
                    f_quick_workout_value.text = mDistanceArray[progress].toString()
                } else if (mType == 2) {
                    f_quick_workout_value.text = mTimeArray[progress].milliSecondsToTimespan()
                }
            }
        })

        if (mType == 1) {
            f_quick_workout_type.text = "Meters"
            f_quick_workout_seekbar.max = mDistanceArray.size - 1
        } else if (mType == 2) {
            f_quick_workout_type.text = "Time"
            f_quick_workout_seekbar.max = mTimeArray.size - 1
        }
        f_quick_workout_seekbar.progress = 0
    }

    companion object {
        fun newInstance(): QuickWorkoutFragment {
            return QuickWorkoutFragment()
        }
    }
}
