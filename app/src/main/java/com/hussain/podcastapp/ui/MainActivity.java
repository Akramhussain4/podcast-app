package com.hussain.podcastapp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.hussain.podcastapp.R;
import com.hussain.podcastapp.adapter.PodcastPagerAdapter;
import com.hussain.podcastapp.base.BaseActivity;
import com.hussain.podcastapp.utils.GlideApp;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.vp)
    ViewPager mViewPager;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onCreate(savedInstanceState, R.layout.activity_main);
        setMFirebaseUser(FirebaseAuth.getInstance().getCurrentUser());
        setUI();
    }

    private void setUI() {
        PodcastPagerAdapter adapter = new PodcastPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        List<String> types = new ArrayList<>();
        types.add("TECH");
        types.add("Science");
        types.add("Health");
        types.add("Business");
        types.add("Sports");
        adapter.refresh(types);
        mTabLayout.setupWithViewPager(mViewPager, true);
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                GlideApp.with(imageView.getContext()).load(getMFirebaseUser().getPhotoUrl()).into(imageView);
            }
        });
        new DrawerBuilder().withActivity(this).build();
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withSelectionListEnabledForSingleProfile(false)
                .withHeaderBackground(R.drawable.header_background)
                .addProfiles(
                        new ProfileDrawerItem().withName(getMFirebaseUser().getDisplayName()).withEmail(getMFirebaseUser().getEmail()).withIcon(getMFirebaseUser().getPhotoUrl())
                )
                .withOnAccountHeaderListener((view, profile, currentProfile) -> false)
                .build();
        new DrawerBuilder()
                .withActivity(this)
                .withToolbar(getMToolbar())
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(1).withName("Home").withIcon(R.drawable.ic_home).withSelectable(true),
                        new PrimaryDrawerItem().withIdentifier(2).withName("Subscriptions").withIcon(R.drawable.ic_heart_box).withSelectable(false),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withIdentifier(3).withIcon(R.drawable.ic_info).withName("About").withSelectable(false)
                )
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    if (drawerItem != null) {
                        switch ((int) drawerItem.getIdentifier()) {
                            case 2:
                                startActivity(new Intent(this, SubscribeActivity.class));
                                break;
                            case 3:
                                startActivity(new Intent(this, AboutActivity.class));
                                break;
                        }
                    }
                    return false;
                })
                .build();
    }


    @Override
    public void onToolBarSetUp(Toolbar toolbar, ActionBar actionBar) {
        toolbar.setTitle("Pod play");
        toolbar.setTitleTextColor(getColor(R.color.colorWhite));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
}
