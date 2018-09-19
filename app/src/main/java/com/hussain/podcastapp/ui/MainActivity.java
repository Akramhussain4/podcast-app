package com.hussain.podcastapp.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.hussain.podcastapp.R;
import com.hussain.podcastapp.adapter.PodcastPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.vp)
    ViewPager mViewPager;
    @BindView(R.id.nts)
    NavigationTabStrip mNavStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setUI();
    }

    private void setUI() {
        PodcastPagerAdapter adapter = new PodcastPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mNavStrip.setAnimationDuration(200);
        mNavStrip.setTitles("TECH","Science","Health","Business","Sports");
        mNavStrip.setViewPager(mViewPager,0);
    }
}
