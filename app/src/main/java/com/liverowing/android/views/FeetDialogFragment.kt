package com.liverowing.android.views

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.liverowing.android.R
import kotlinx.android.synthetic.main.dialog_feets.*
import kotlinx.android.synthetic.main.dialog_feets.view.*

class FeetDialogFragment : DialogFragment() {

    lateinit var listener: NumberPickerListener

    var feets: Int = 5
        get() = numberpicker_feet.value

    var inchs: Int = 6
        get() = numberpicker_inch.value

    companion object {
        fun newInstance(listener: NumberPickerListener): FeetDialogFragment {
            val f = FeetDialogFragment()
            f.listener = listener
            return f
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_feets, container, false)
        setupUI(view)
        return view
    }

    private fun setupUI(view: View) {
        view.a_dialog_feets_cancel.setOnClickListener {
            dismiss()
        }

        view.a_dialog_feets_ok.setOnClickListener {
            val value = String.format("%d'%d″", feets, inchs)
            listener.onNumberPickerSelected(value)
            dismiss()
        }
    }

}
