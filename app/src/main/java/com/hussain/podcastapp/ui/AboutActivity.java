package com.hussain.podcastapp.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.hussain.podcastapp.R;
import com.hussain.podcastapp.base.BaseActivity;

public class AboutActivity extends BaseActivity {

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onCreate(savedInstanceState, R.layout.activity_about);
    }

    @Override
    public void onToolBarSetUp(Toolbar toolbar, ActionBar actionBar) {
        TextView tvHeader = toolbar.findViewById(R.id.tvClassName);
        tvHeader.setText(R.string.app_name);
    }
}
