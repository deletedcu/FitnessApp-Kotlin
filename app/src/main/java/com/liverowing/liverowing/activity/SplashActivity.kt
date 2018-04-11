package com.liverowing.liverowing.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.liverowing.liverowing.R
import com.liverowing.liverowing.RaceTestActivity
import com.liverowing.liverowing.activity.login.AppAuthLogbook
import com.liverowing.liverowing.activity.login.LoginRegisterActivity
import com.liverowing.liverowing.activity.login.LoginRegisterIntent
import com.parse.ParseUser

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val intent = if (ParseUser.getCurrentUser() === null) {
            Intent(this, LoginRegisterActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
            //Intent(this, RaceTestActivity::class.java)
            //Intent(this, AppAuthLogbook::class.java)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}
