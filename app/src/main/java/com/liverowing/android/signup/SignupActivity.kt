package com.liverowing.android.signup

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import com.kaopiz.kprogresshud.KProgressHUD
import com.liverowing.android.LiveRowing
import com.liverowing.android.R
import com.liverowing.android.activity.login.SignupStep1Fragment
import com.liverowing.android.activity.login.SignupStep2Fragment
import com.liverowing.android.activity.login.SignupStep3Fragment
import com.liverowing.android.activity.login.SignupStep4Fragment
import com.liverowing.android.model.parse.User
import com.liverowing.android.signup.fragments.BaseStepFragment
import com.liverowing.android.signup.fragments.ResultListener
import com.liverowing.android.util.Utils
import com.parse.ParseException
import com.parse.ParseFile
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class SignupActivity: MvpActivity<SignupView, SignupPresenter>(), SignupView, ResultListener {

    var currentStep: Int = 1
    var currentFragment: BaseStepFragment? = null

    var newUser: User = User()
    var password: String = ""

    private lateinit var hud: KProgressHUD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        window.statusBarColor = Color.TRANSPARENT
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        hud = KProgressHUD.create(this)
        setupUI()
    }

    fun setupUI() {
        btn_fuck_this.setOnClickListener {
            if (currentStep > 1) {
                supportFragmentManager.popBackStack()
                currentStep --
                currentFragment = supportFragmentManager.findFragmentByTag(currentStep.toString()) as BaseStepFragment?
                a_signup_stepbar.currentStep = currentStep
            } else {
                finish()
            }
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
            1 -> SignupStep1Fragment(this)
            2 -> SignupStep2Fragment(this)
            3 -> SignupStep3Fragment(this)
            4 -> SignupStep4Fragment(this)
            else -> null
        }

        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.layout_signup_page, currentFragment!!, currentStep.toString())
                .addToBackStack(null)
                .commit()

        if (currentStep == 4) {
            (currentFragment as SignupStep4Fragment).userName = newUser.username
        }

    }

    override fun createPresenter() = SignupPresenter()

    override fun onResultListener(state: Boolean, data: HashMap<String, String>) {
        if (state) {
            when (currentStep) {
                1 -> {
                    newUser.username = data.get("username")
                    newUser.email = data.get("email")
                    currentStep ++
                    a_signup_stepbar.currentStep = currentStep
                    updateFragment()
                }
                2 -> {
                    newUser.setPassword(data.get("password"))
                    password = data.get("password")!!
                    currentStep ++
                    a_signup_stepbar.currentStep = currentStep
                    updateFragment()
                }
                3 -> {
                    newUser.weight = data.get("weight")!!.toInt()
                    newUser.height = data.get("height")!!.toInt()
                    newUser.isMetric = data.get("isMetric")!!.toBoolean()
                    currentStep ++
                    a_signup_stepbar.currentStep = currentStep
                    updateFragment()
                }
                4 -> {
                    val birthday = data.get("birthday")
                    val pattern = "MM/dd/yyyy"
                    var simpleDateFormat = SimpleDateFormat(pattern, Locale.US)
                    newUser.dob = simpleDateFormat.parse(birthday)
                    newUser.gender = data.get("gender")

//                    if (data.containsKey("image")) {
//                        val encodeString = data.get("image")
//                        if (encodeString != null) {
//                            val bitmap = Utils.StringToBitmap(encodeString)
//                            val baos = ByteArrayOutputStream()
//                            if (bitmap != null) {
//                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
//                                val bytes= baos.toByteArray()
//                                val parseFile = ParseFile("profilePicture.png", bytes)
//                                newUser.image = parseFile
//                            }
//                        }
//                    }

                    presenter.signup(newUser, password)
                }
                else -> {}
            }
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
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun signupuccessful(user: ParseUser) {
        hud.dismiss()
        setResult(Activity.RESULT_OK)
        finish()
    }

}