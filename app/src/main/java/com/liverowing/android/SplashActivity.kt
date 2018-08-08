package com.liverowing.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.liverowing.android.loggedout.LoggedOutActivity
import com.parse.ParseUser


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityIntent: Intent = if (ParseUser.getCurrentUser() != null) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, LoggedOutActivity::class.java)
        }

        startActivity(activityIntent)
        finish()
    }
}
