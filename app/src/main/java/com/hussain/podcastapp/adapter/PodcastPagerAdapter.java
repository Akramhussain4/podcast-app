package com.hussain.podcastapp.adapter;

import android.os.Bundle;

import com.hussain.podcastapp.ui.PodcastListFragment;
import com.hussain.podcastapp.utils.AppConstants;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PodcastPagerAdapter extends FragmentPagerAdapter {

    public PodcastPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                PodcastListFragment techFragment = new PodcastListFragment();
                Bundle techB = new Bundle();
                techB.putString(AppConstants.CATEGORY_KEY, AppConstants.TECH_GENRE);
                techFragment.setArguments(techB);
                return techFragment;
            case 1:
                PodcastListFragment scienceFragment = new PodcastListFragment();
                Bundle sciB = new Bundle();
                sciB.putString(AppConstants.CATEGORY_KEY, AppConstants.SCIENCE_GENRE);
                scienceFragment.setArguments(sciB);
                return scienceFragment;
            case 2:
                PodcastListFragment healthFragment = new PodcastListFragment();
                Bundle healB = new Bundle();
                healB.putString(AppConstants.CATEGORY_KEY, AppConstants.HEALTH_GENRE);
                healthFragment.setArguments(healB);
                return healthFragment;
            case 3:
                PodcastListFragment businessFragment = new PodcastListFragment();
                Bundle bussB = new Bundle();
                bussB.putString(AppConstants.CATEGORY_KEY, AppConstants.BUSINESS_GENRE);
                businessFragment.setArguments(bussB);
                return businessFragment;
            case 4:
                PodcastListFragment sportsFragment = new PodcastListFragment();
                Bundle sportB = new Bundle();
                sportB.putString(AppConstants.CATEGORY_KEY, AppConstants.SPORTS_GENRE);
                sportsFragment.setArguments(sportB);
                return sportsFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }
}
