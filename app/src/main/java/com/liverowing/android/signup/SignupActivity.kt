package com.liverowing.android.signup

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.liverowing.android.R
import com.liverowing.android.activity.login.SignupStep1Fragment
import com.liverowing.android.activity.login.SignupStep2Fragment
import com.liverowing.android.activity.login.SignupStep3Fragment
import com.liverowing.android.activity.login.SignupStep4Fragment
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity: AppCompatActivity() {

    var currentStep: Int = 1
    var currentFragment: Fragment? = null

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
            if (currentStep < 4) {
                currentStep ++
                a_signup_stepbar.currentStep = currentStep
                updateFragment()
            } else {
                finish()
            }
        }

        currentStep = 1
        a_signup_stepbar.maxCount = 4
        a_signup_stepbar.currentStep = currentStep
        updateFragment()
    }

    fun updateFragment() {
        currentFragment = when (currentStep) {
            1 -> SignupStep1Fragment()
            2 -> SignupStep2Fragment()
            3 -> SignupStep3Fragment()
            4 -> SignupStep4Fragment()
            else -> Fragment()
        }

        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.layout_signup_page, currentFragment!!)
                .commit()


    }
}