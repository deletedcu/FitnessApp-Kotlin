package com.liverowing.android.workouthistory.bottomSheet

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.liverowing.android.R
import com.liverowing.android.model.parse.Workout
import kotlinx.android.synthetic.main.bottom_sheet.view.*


class BottomSheetFragment : BottomSheetDialogFragment() {

    lateinit var listener: BottomSheetListener
    lateinit var workout: Workout

    companion object {
        fun newInstance(workout: Workout, listener: BottomSheetListener): BottomSheetFragment {
            var f = BottomSheetFragment()
            f.listener = listener
            f.workout = workout
            return f
        }
    }

    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimationEnterExit
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.bottom_sheet, container, false)
        setupUI(v)
        return v
    }

    private fun setupUI(view: View) {
        view.bottom_sheet_view_details.setOnClickListener {
            listener.onViewClick(workout)
            dismiss()
        }

        view.bottom_sheet_share_friend.setOnClickListener {
            listener.onShareToFriend(workout)
            dismiss()
        }

        view.bottom_sheet_share_social.setOnClickListener {
            listener.onShareToSocial(workout)
            dismiss()
        }

        view.bottom_sheet_share_concept2.setOnClickListener {
            listener.onShareToConcept2(workout)
            dismiss()
        }

        view.bottom_sheet_send_strava.setOnClickListener {
            listener.onSendToStrava(workout)
            dismiss()
        }

        view.bottom_sheet_delete.setOnClickListener {
            listener.onDeleteWorkout(workout)
            dismiss()
        }

        view.bottom_sheet_share_friend_check.isEnabled = false
        view.bottom_sheet_share_social_check.isEnabled = false
        view.bottom_sheet_share_concept2_check.isEnabled = false
        view.bottom_sheet_send_strava_check.isEnabled = true
    }
}