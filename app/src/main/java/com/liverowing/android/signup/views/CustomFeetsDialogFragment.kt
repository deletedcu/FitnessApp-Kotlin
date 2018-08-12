package com.liverowing.android.signup.views

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.liverowing.android.R
import kotlinx.android.synthetic.main.dialog_feets.*
import kotlinx.android.synthetic.main.dialog_feets.view.*

class CustomFeetsDialogFragment(var listener: NumberPickerListener) : DialogFragment() {

    var feets: Int = 5
        get() = numberpicker_feet.value
        set(value) {
            field = value
        }

    var inchs: Int = 6
        get() = numberpicker_inch.value
        set(value) {
            field = value
        }

    companion object {
        fun newInstance(listener: NumberPickerListener): CustomFeetsDialogFragment {
            val f = CustomFeetsDialogFragment(listener)
            return f
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
