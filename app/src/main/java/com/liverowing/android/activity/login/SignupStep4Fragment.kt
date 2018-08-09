package com.liverowing.android.activity.login

import android.app.DatePickerDialog
import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liverowing.android.R
import kotlinx.android.synthetic.main.fragment_signup_4.*
import java.text.SimpleDateFormat
import java.util.*

class SignupStep4Fragment: Fragment() {

    var myCalendar: Calendar = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup_4, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(year, month, dayOfMonth)
            updateBirthday()
        }

        a_signup_birthday.setOnClickListener {
            DatePickerDialog(activity, dateSetListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun updateBirthday() {
        val pattern = "MM/dd/yyyy"
        var simpleDateFormat = SimpleDateFormat(pattern, Locale.US)
        a_signup_birthday.setText(simpleDateFormat.format(myCalendar.time))
    }
}