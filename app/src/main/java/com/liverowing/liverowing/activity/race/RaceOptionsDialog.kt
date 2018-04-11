package com.liverowing.liverowing.activity.race

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liverowing.liverowing.R
import kotlinx.android.synthetic.main.fragment_race_options.*

/**
 * Created by henrikmalmberg on 22/03/2018.
 */
class RaceOptionsDialog : BottomSheetDialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_race_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        f_race_options_cancel.setOnClickListener {
            dismiss()
        }
    }
}
