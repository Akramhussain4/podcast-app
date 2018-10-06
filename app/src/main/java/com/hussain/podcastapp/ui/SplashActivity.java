package com.hussain.podcastapp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.hussain.podcastapp.R;
import com.hussain.podcastapp.base.BaseActivity;

import java.util.Arrays;
import java.util.List;

public class SplashActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 123;
    private Boolean Registered;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onCreate(savedInstanceState, R.layout.activity_splash);
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Registered = sharedPref.getBoolean("Registered", false);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (Registered) {
                launchMainActivity();
            } else {
                login();
            }
        }, 2000);
    }

    private void login() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build());
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.logo_transparent)
                        .setTheme(R.style.AppTheme)
                        .build(),
                RC_SIGN_IN);
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("Registered", true);
                editor.apply();
                launchMainActivity();
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Toast.makeText(this, "Problem Signing In!!!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onToolBarSetUp(Toolbar toolbar, ActionBar actionBar) {

    }
}
