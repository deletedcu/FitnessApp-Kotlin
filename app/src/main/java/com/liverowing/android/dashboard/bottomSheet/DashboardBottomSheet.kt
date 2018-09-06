package com.liverowing.android.dashboard.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.liverowing.android.R
import com.liverowing.android.model.parse.WorkoutType
import kotlinx.android.synthetic.main.bottom_sheet.view.*
import kotlinx.android.synthetic.main.dashboard_bottom_sheet.view.*


class DashboardBottomSheet(private var workoutType: WorkoutType, private var listener: DashboardBottomSheetListener) : BottomSheetDialogFragment() {

    override fun onActivityCreated(arg0: Bundle?) {
        super.onActivityCreated(arg0)
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimationEnterExit
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.dashboard_bottom_sheet, container, false)
        setupUI(v)
        return v
    }

    private fun setupUI(view: View) {
        view.dashboard_bottom_sheet_bookmark.setOnClickListener {
            listener.onBookMarkClick(workoutType)
            dismiss()
        }

        view.dashboard_bottom_sheet_share.setOnClickListener {
            listener.onShareClick(workoutType)
            dismiss()
        }

        view.dashboard_bottom_sheet_send_friend.setOnClickListener {
            listener.onSendClick(workoutType)
            dismiss()
        }

        view.dashboard_bottom_sheet_close.setOnClickListener {
            dismiss()
        }
    }
}