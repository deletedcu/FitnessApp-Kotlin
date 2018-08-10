package com.liverowing.android.signup

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.fragment.app.Fragment
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import com.liverowing.android.R
import com.liverowing.android.activity.login.SignupStep1Fragment
import com.liverowing.android.activity.login.SignupStep2Fragment
import com.liverowing.android.activity.login.SignupStep3Fragment
import com.liverowing.android.activity.login.SignupStep4Fragment
import com.liverowing.android.signup.fragments.BaseStepFragment
import com.liverowing.android.signup.fragments.ResultListener
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity: MvpActivity<SignupView, SignupPresenter>(), SignupView, ResultListener {

    var currentStep: Int = 1
    var currentFragment: BaseStepFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        window.statusBarColor = Color.TRANSPARENT
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        setupUI()
    }

    fun setupUI() {
        btn_fuck_this.setOnClickListener {
            finish()
        }

        btn_next.setOnClickListener {

            currentFragment!!.checkValidation()
//            if (currentStep < 4) {
//                currentStep ++
//                a_signup_stepbar.currentStep = currentStep
//                updateFragment()
//            } else {
//                finish()
//            }
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
                .replace(R.id.layout_signup_page, currentFragment!!)
                .commit()


    }

    override fun createPresenter() = SignupPresenter()

    override fun showError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onResultListener(state: Boolean, data: HashMap<String, String>?) {
        Log.d("OnResultListener", data.toString())
    }
}