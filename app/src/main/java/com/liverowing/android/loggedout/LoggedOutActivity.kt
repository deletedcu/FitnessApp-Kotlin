package com.liverowing.android.loggedout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.liverowing.android.BaseMvpFragment
import com.liverowing.android.R

class LoggedOutActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loggedout)
    }

    override fun onResume() {
        super.onResume()
        navController = Navigation.findNavController(this, R.id.a_loggedout_content)
    }

    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.a_loggedout_content)
        var handled = false
        val fragmentList = navHostFragment?.childFragmentManager?.fragments
        if (fragmentList != null) {
            val fragment = fragmentList.get(0)
            if (fragment is BaseMvpFragment<*, *>) {
                fragment.onBackPressed()
                handled = true
            }
        }
        if (!handled) {
            super.onBackPressed()
        }
    }
}
