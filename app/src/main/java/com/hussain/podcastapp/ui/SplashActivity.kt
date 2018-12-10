package com.hussain.podcastapp.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.hussain.podcastapp.R
import com.hussain.podcastapp.base.BaseActivity
import com.hussain.podcastapp.service.JobScheduler
import com.hussain.podcastapp.utils.SharedPrefUtil
import java.util.*

class SplashActivity : BaseActivity() {
    private var Registered: Boolean? = null

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        onCreate(savedInstanceState, R.layout.activity_splash)
        Registered = SharedPrefUtil(this).getIsRegistered()
        Handler(Looper.getMainLooper()).postDelayed({
            if (Registered!!) {
                launchMainActivity()
                JobScheduler.getInstance()!!.scheduleJob("notification", this)
            } else {
                login()
            }
        }, 2000)
    }

    private fun login() {
        val providers = Arrays.asList(
                AuthUI.IdpConfig.GoogleBuilder().build(),
                AuthUI.IdpConfig.EmailBuilder().build())
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.logo_transparent)
                        .setTheme(R.style.AppTheme)
                        .build(),
                RC_SIGN_IN)
    }

    private fun launchMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                mFirebaseUser = FirebaseAuth.getInstance().currentUser
                SharedPrefUtil(this).setIsRegistered(true)
                launchMainActivity()
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Toast.makeText(this, "Problem Signing In!!!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onToolBarSetUp(toolbar: Toolbar?, actionBar: ActionBar) {

    }

    companion object {

        private val RC_SIGN_IN = 123
    }
}
