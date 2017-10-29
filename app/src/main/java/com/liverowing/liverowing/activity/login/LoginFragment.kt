package com.liverowing.liverowing.activity.login


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liverowing.liverowing.R
import com.liverowing.liverowing.activity.MainIntent
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
            Log.d("LiveRowing", "test")
            ParseUser.logInInBackground(
                    f_login_txt_account.editText?.text.toString(),
                    f_login_txt_password.editText?.text.toString(),
                    { user: ParseUser?, e: ParseException? ->
                        if (e !== null) {
                            Snackbar.make(view!!, e.message.toString(), Snackbar.LENGTH_LONG).show()
                        } else {
                            val intent: Intent = activity.MainIntent()
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                            activity.finish()
                        }
                    })
        }
    }

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }
}
