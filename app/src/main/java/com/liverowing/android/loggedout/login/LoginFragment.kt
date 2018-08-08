package com.liverowing.android.loggedout.login


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.navigation.findNavController
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import com.liverowing.android.LiveRowing
import com.liverowing.android.MainActivity
import com.liverowing.android.R
import com.parse.ParseException
import com.parse.ParseUser
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : MvpFragment<LoginView, LoginPresenter>(), LoginView, View.OnClickListener {
    override fun createPresenter() = LoginPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        f_login_forgot_password.setOnClickListener(this@LoginFragment)
        f_login_signin_signup.setOnClickListener(this@LoginFragment)
        f_login_login_button.setOnClickListener(this@LoginFragment)
    }

    private fun login() {
        val username = f_login_username_text.text.toString()
        val password = f_login_password_text.text.toString()

        presenter.login(username, password)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.f_login_forgot_password -> {
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, f_login_logo, "logo")
                view?.findNavController()?.navigate(R.id.action_loginFragment_to_forgotPasswordFragment, options.toBundle())
            }

            R.id.f_login_signin_signup -> {
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, f_login_logo, "logo")
                view?.findNavController()?.navigate(R.id.action_loginFragment_to_signupFragment, options.toBundle())
            }

            R.id.f_login_login_button -> login()
        }

    }

    override fun showLoginForm() {
        f_login_login_button.isEnabled = true
    }

    override fun showLoading() {
        f_login_login_button.isEnabled = false
    }

    override fun showError(e: ParseException) {
        f_login_login_button.isEnabled = true
        val message = when(e.code) {
            ParseException.USERNAME_MISSING -> "Username/email is required"
            ParseException.PASSWORD_MISSING -> "Password is required"
            ParseException.OBJECT_NOT_FOUND -> "Invalid credentials"
            else -> LiveRowing.parseErrorMessageFromException(e)
        }

        if (message.isNotEmpty()) {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun loginSuccessful(user: ParseUser) {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.supportFinishAfterTransition()
    }
}
