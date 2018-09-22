package com.hussain.podcastapp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.gigamole.navigationtabstrip.NavigationTabStrip;
import com.hussain.podcastapp.R;
import com.hussain.podcastapp.adapter.PodcastPagerAdapter;
import com.hussain.podcastapp.base.BaseActivity;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.vp)
    ViewPager mViewPager;
    @BindView(R.id.nts)
    NavigationTabStrip mNavStrip;


    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onCreate(savedInstanceState, R.layout.activity_main);
        setUI();
    }

    private void setUI() {
        PodcastPagerAdapter adapter = new PodcastPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mNavStrip.setAnimationDuration(200);
        mNavStrip.setTitles("TECH", "Science", "Health", "Business", "Sports");
        mNavStrip.setViewPager(mViewPager, 0);
        new DrawerBuilder().withActivity(this).build();
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withSelectionListEnabledForSingleProfile(false)
                .withHeaderBackground(R.drawable.ic_launcher_background)
                .addProfiles(
                        new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com").withIcon(R.drawable.ic_launcher_foreground)
                )
                .withOnAccountHeaderListener((view, profile, currentProfile) -> false)
                .build();
        new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(1).withName("Home"),
                        new PrimaryDrawerItem().withIdentifier(2).withName("Subscriptions"),
                        new PrimaryDrawerItem().withIdentifier(3).withName("Offline Podcasts"),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withIdentifier(4).withName("About")
                )
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    if (drawerItem != null) {
                        if (drawerItem.getIdentifier() == 4) {
                            startActivity(new Intent(this, AboutActivity.class));
                        }
                    }
                    return false;
                })
                .build();
    }

    @Override
    public void onToolBarSetUp(Toolbar toolbar, ActionBar actionBar) {
        TextView tvHeader = toolbar.findViewById(R.id.tvClassName);
        tvHeader.setText(R.string.app_name);
    }
}
