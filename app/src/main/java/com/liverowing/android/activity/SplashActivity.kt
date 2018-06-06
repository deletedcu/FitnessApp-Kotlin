package com.liverowing.android.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.liverowing.android.R
import com.liverowing.android.activity.login.LoginActivity
import com.parse.ParseUser

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val intent = if (ParseUser.getCurrentUser() === null) {
            Intent(this, LoginActivity::class.java)
        } else {
            //Intent(this, LoginActivity::class.java)
            Intent(this, MainActivity::class.java)
            //Intent(this, RaceTestActivity::class.java)
            //Intent(this, AppAuthLogbook::class.java)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}
