package com.liverowing.liverowing.activity.login


import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liverowing.liverowing.R
import com.parse.ParseException
import com.parse.ParseUser
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater!!.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        f_login_button_login.setOnClickListener {
            ParseUser.logInInBackground(
                    f_login_txt_account.editText?.text.toString(),
                    f_login_txt_password.editText?.text.toString(),
                    { user: ParseUser?, e: ParseException? ->
                        if (e !== null) {
                            Snackbar.make(view!!, e.message.toString(), Snackbar.LENGTH_LONG).show()
                        } else {
                            Log.d("LiveRowing", user?.username)
                        }
                    })
        }
    }

    companion object {
        fun newInstance(): LoginFragment {
            val fragment = LoginFragment()
            return fragment
        }
    }
}
