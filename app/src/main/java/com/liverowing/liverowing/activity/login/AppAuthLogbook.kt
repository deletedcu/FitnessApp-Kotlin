package com.liverowing.liverowing.activity.login

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.liverowing.liverowing.R
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import java.net.URI
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationResponse
import android.content.Intent
import kotlinx.android.synthetic.main.activity_app_auth_logbook.*
import net.openid.appauth.AuthorizationService

class AppAuthLogbook : AppCompatActivity() {
    lateinit var authRequest: AuthorizationRequest

    companion object {
        const val RC_AUTH = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_auth_logbook)

        val serviceConfig = AuthorizationServiceConfiguration(
                Uri.parse("https://log.concept2.com/oauth/authorize"), // authorization endpoint
                Uri.parse("https://log.concept2.com/oauth/access_token")) // token endpoint

        val authRequestBuilder = AuthorizationRequest.Builder(
                serviceConfig,
                "C7sp4c4NCJf60ho4n00Rwuyh4w2G3vFY2cltWPPH",
                ResponseTypeValues.CODE,
                Uri.parse("com.liverowing.android://logbook"))

        authRequest = authRequestBuilder.build()

        a_logbook_login.setOnClickListener {
            doAuthorization()
        }
    }

    private fun doAuthorization() {
        val authService = AuthorizationService(this)
        val authIntent = authService.getAuthorizationRequestIntent(authRequest)
        startActivityForResult(authIntent, RC_AUTH)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == RC_AUTH) {
            val resp = AuthorizationResponse.fromIntent(data)
            val ex = AuthorizationException.fromIntent(data)
            // ... process the response or exception ...
        } else {
            // ...
        }
    }
}
