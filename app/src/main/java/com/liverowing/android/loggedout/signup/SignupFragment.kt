package com.liverowing.android.loggedout.signup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import com.kaopiz.kprogresshud.KProgressHUD
import com.liverowing.android.LiveRowing
import com.liverowing.android.MainActivity
import com.liverowing.android.R
import com.liverowing.android.base.BaseMvpFragment
import com.liverowing.android.loggedout.signup.fragments.*
import com.liverowing.android.model.parse.User
import com.liverowing.android.util.Constants
import com.liverowing.android.util.Utils
import com.parse.ParseException
import kotlinx.android.synthetic.main.fragment_signup.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class SignupFragment: BaseMvpFragment<SignupView, SignupPresenter>(), SignupView, ResultListener {

    var currentStep: Int = 1
    var currentFragment: BaseStepFragment? = null

    var newUser: User = User()
    var password: String = ""

    private lateinit var hud: KProgressHUD

    override fun createPresenter() = SignupPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hud = KProgressHUD.create(this.context)
        setupUI()
    }

    override fun onBackPressed() {
        back()
    }

    fun setupUI() {
        view!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                Utils.hideKeyboard(activity!!)
            }
        })

        btn_back.setOnClickListener {
            back()
        }

        btn_next.setOnClickListener {
            currentFragment!!.checkValidation()
        }

        currentStep = 1
        a_signup_stepbar.maxCount = 4
        a_signup_stepbar.currentStep = currentStep
        updateFragment()
    }

    fun updateFragment() {
        currentFragment = when (currentStep) {
            1 -> SignupStep1Fragment.newInstance(this)
            2 -> SignupStep2Fragment.newInstance(this)
            3 -> SignupStep3Fragment.newInstance(this)
            4 -> SignupStep4Fragment.newInstance(this)
            else -> null
        }

        fragmentManager!!
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.layout_signup_page, currentFragment!!, currentStep.toString())
                .addToBackStack(null)
                .commit()

        if (currentStep == 4) {
            (currentFragment as SignupStep4Fragment).userName = newUser.username
        }

    }

    private fun back() {
        if (currentStep > 1) {
            fragmentManager!!.popBackStack()
            currentStep --
            currentFragment = fragmentManager!!.findFragmentByTag(currentStep.toString()) as BaseStepFragment?
            a_signup_stepbar.currentStep = currentStep
        } else {
            view!!.findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }
    }

    override fun onResultListener(state: Boolean, data: HashMap<String, String>?) {
        if (state) when (currentStep) {
            1 -> {
                newUser.username = data!!.get("username")
                newUser.displayName = newUser.username
                newUser.email = data.get("email")
                currentStep ++
                a_signup_stepbar.currentStep = currentStep
                updateFragment()
            }
            2 -> {
                newUser.setPassword(data!!.get("password"))
                password = data.get("password")!!
                currentStep ++
                a_signup_stepbar.currentStep = currentStep
                updateFragment()
            }
            3 -> {
                newUser.weight = data!!.get("weight")!!.toInt()
                newUser.height = data.get("height")!!.toDouble()
                newUser.isMetric = data.get("isMetric")!!.toBoolean()
                currentStep ++
                a_signup_stepbar.currentStep = currentStep
                updateFragment()
            }
            4 -> {
                val birthday = data!!.get("birthday")
                val simpleDateFormat = SimpleDateFormat(Constants.DATE_PATTERN, Locale.US)
                newUser.dob = simpleDateFormat.parse(birthday)
                newUser.gender = data.get("gender")
                val bitmap = (currentFragment as SignupStep4Fragment).myBitmap
                presenter.signup(newUser, password, bitmap)
            }
            else -> {}
        }
    }

    override fun showLoading() {
        hud.show()
    }

    override fun showError(e: ParseException) {
        hud.dismiss()
        val message = when(e.code) {
            ParseException.USERNAME_MISSING -> "Username/email is required"
            ParseException.PASSWORD_MISSING -> "Password is required"
            ParseException.OBJECT_NOT_FOUND -> "Invalid credentials"
            else -> LiveRowing.parseErrorMessageFromException(e)
        }

        if (message.isNotEmpty()) {
            Toast.makeText(this.context, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun signupuccessful() {
        hud.dismiss()
        AlertDialog.Builder(this.context!!)
                .setTitle("Signup successful!")
                .setMessage("You have successfully signed up with LiveRowing and are now signed in.")
                .setPositiveButton("OK") { _, _ ->
                    startMainActivity()
                }
                .create()
                .show()
    }

    private fun startMainActivity() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.supportFinishAfterTransition()
    }

}