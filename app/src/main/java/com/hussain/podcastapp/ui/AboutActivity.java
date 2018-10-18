package com.hussain.podcastapp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.hussain.podcastapp.R;
import com.hussain.podcastapp.base.BaseActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import butterknife.OnClick;

public class AboutActivity extends BaseActivity {

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onCreate(savedInstanceState, R.layout.activity_about);
    }

    @Override
    public void onToolBarSetUp(Toolbar toolbar, ActionBar actionBar) {
        TextView tvHeader = toolbar.findViewById(R.id.tvClassName);
        tvHeader.setText(R.string.about);
    }

    @OnClick(R.id.tvEmail)
    public void emailClick() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "akramhussain0007@gmail.com"));
        startActivity(intent);
    }

    @OnClick(R.id.tvLinkedIn)
    public void linkedInClick() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/akramhussain4/"));
        startActivity(intent);
    }

    @OnClick(R.id.tvGitHub)
    public void githubClick() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Akramhussain4"));
        startActivity(intent);
    }

    @OnClick(R.id.tvGooglePlay)
    public void googlePlayClick() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=6822584155686265410"));
        startActivity(intent);
    }

    @OnClick(R.id.tvCoffee)
    public void coffeeClick() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.buymeacoffee.com/akramhussain4"));
        startActivity(intent);
    }
}
