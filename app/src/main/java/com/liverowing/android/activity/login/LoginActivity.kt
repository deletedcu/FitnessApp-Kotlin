package com.liverowing.android.activity.login

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.liverowing.android.R
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.liverowing.android.activity.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.facebook.login.LoginResult
import com.facebook.login.LoginManager
import com.kaopiz.kprogresshud.KProgressHUD
import com.liverowing.android.LiveRowing
import com.liverowing.android.extensions.default
import com.parse.*


class LoginActivity : AppCompatActivity() {
    private lateinit var mCallbackManager: CallbackManager
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var hud: KProgressHUD

    companion object {
        const val RC_GOOGLE_SIGN_IN = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        hud = KProgressHUD.create(this).default()

        window.statusBarColor = Color.TRANSPARENT
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        // ** GOOGLE **
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("328913119568-0ncg5qt8gt2dgkeasi00q6oqtahulpe3.apps.googleusercontent.com")
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        a_login_google_signin.setOnClickListener {
            hud.show()
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        }


        // ** FACEBOOK **
        mCallbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                handleFacebookSignInResult(loginResult)
            }

            override fun onCancel() {
                Log.d("LiveRowing", "Facebook cancel")
            }

            override fun onError(exception: FacebookException) {
                Log.d("LiveRowing", "Facebook error: " + exception.message)
            }
        })

        a_login_facebook_login.setOnClickListener {
            hud.show()
            LoginManager.getInstance().logInWithReadPermissions(this@LoginActivity, listOf("email", "public_profile"))
        }


        a_login_password.editText!!.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (event != null) {
                hud.show()
                val username = a_login_username.editText!!.text.toString()
                val password = a_login_password.editText!!.text.toString()
                ParseUser.logInInBackground(username, password) { _, e ->
                    hud.dismiss()
                    if (e != null) {
                        when (e.code) {
                            ParseException.OBJECT_NOT_FOUND -> showErrorToast("Invalid username and/or password.")
                            else -> LiveRowing.globalParseExceptionHandler(this@LoginActivity, e)
                        }
                    } else {
                        loginSuccess()
                    }
                }
                return@OnEditorActionListener true
            }

            false
        })

        a_login_signup_button.setOnClickListener {
            var intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        /*
        val intent = Intent(activity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                            activity!!.finish()
         */
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            handleGoogleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(data))
        }
    }

    private fun handleFacebookSignInResult(result: LoginResult) {
        GraphRequest.newGraphPathRequest(result.accessToken, "/me?fields=id,name,email") { response ->
            if (response.error == null) {
                if (response.jsonObject.has("email")) {
                    val email = response.jsonObject.get("email")
                    ParseCloud.callFunctionInBackground<List<String>>("checkUserAuthOptions", mapOf("email" to email))
                            .continueWithTask {
                                if (it.result.isEmpty() || it.result.contains("facebook")) {
                                    val authData = mapOf(
                                            "id" to result.accessToken.userId,
                                            "access_token" to result.accessToken.token,
                                            "expiration_date" to result.accessToken.expires.toString(),
                                            "last_refresh_date" to result.accessToken.lastRefresh.toString(),
                                            "permissions" to result.accessToken.permissions.joinToString(",")
                                    )

                                    ParseUser.logInWithInBackground("facebook", authData)
                                } else {
                                    runOnUiThread {
                                        showErrorToast("Please login using ${it.result.joinToString(" or ")} and then link your account with Facebook.")
                                    }
                                    null
                                }
                            }
                            .onSuccess {
                                when {
                                    it.isCompleted -> {
                                        if (it.result.isNew) {
                                            socialLoginSuccess()
                                        } else {
                                            loginSuccess()
                                        }
                                    }
                                    it.isFaulted -> showErrorToast("Error when linking with Facebook account: ${it.error.message}")
                                    else -> showErrorToast("Unknown error when linking with Facebook account.")
                                }
                            }
                } else {
                    showErrorToast("Unable to retrieve email from Facebook")
                }
            } else {
                showErrorToast("Error: ${response.error.errorMessage}")
            }
        }.executeAsync()
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        if (completedTask.isSuccessful) {
            val account = completedTask.result
            ParseCloud.callFunctionInBackground<List<String>>("checkUserAuthOptions", mapOf("email" to account.email))
                    .continueWithTask {
                        if (it.result.isEmpty() || it.result.contains("google")) {
                            val authData = mapOf(
                                    "id" to account.id,
                                    "id_token" to account.idToken
                            )

                            ParseUser.logInWithInBackground("google", authData)
                        } else {
                            showErrorToast("Please login using ${it.result.joinToString(" or ")} and then link your account with Google.")
                            null
                        }
                    }
                    .onSuccess {
                        when {
                            it.isCompleted -> {
                                if (it.result.isNew) {
                                    socialLoginSuccess()
                                } else {
                                    loginSuccess()
                                }
                            }

                            it.isFaulted -> showErrorToast("Error when linking with Google account: ${it.error.message}")
                            else -> showErrorToast("Unknown error when linking with Google account.")
                        }
                    }
        } else if (completedTask.exception is Exception) {
            showErrorToast("Error when logging in to Google: ${completedTask.exception!!.message}")
        }
    }

    private fun socialLoginSuccess() {
        hud.dismiss()
    }

    private fun loginSuccess() {
        hud.dismiss()
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        this.finish()
    }

    private fun showErrorToast(text: String) {
        hud.dismiss()
        runOnUiThread {
            Toast.makeText(this@LoginActivity, text, Toast.LENGTH_LONG).show()
        }
    }
}
