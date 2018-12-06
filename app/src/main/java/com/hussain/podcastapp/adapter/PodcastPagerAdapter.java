package com.hussain.podcastapp.adapter;

import android.os.Bundle;

import com.hussain.podcastapp.ui.PodcastListFragment;
import com.hussain.podcastapp.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PodcastPagerAdapter extends FragmentPagerAdapter {

    private List<String> list;

    public PodcastPagerAdapter(FragmentManager fm) {
        super(fm);
        list = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                PodcastListFragment techFragment = new PodcastListFragment();
                Bundle techB = new Bundle();
                techB.putString(AppConstants.INSTANCE.getCATEGORY_KEY(), AppConstants.INSTANCE.getTECH_GENRE());
                techFragment.setArguments(techB);
                return techFragment;
            case 1:
                PodcastListFragment scienceFragment = new PodcastListFragment();
                Bundle sciB = new Bundle();
                sciB.putString(AppConstants.INSTANCE.getCATEGORY_KEY(), AppConstants.INSTANCE.getSCIENCE_GENRE());
                scienceFragment.setArguments(sciB);
                return scienceFragment;
            case 2:
                PodcastListFragment healthFragment = new PodcastListFragment();
                Bundle healB = new Bundle();
                healB.putString(AppConstants.INSTANCE.getCATEGORY_KEY(), AppConstants.INSTANCE.getHEALTH_GENRE());
                healthFragment.setArguments(healB);
                return healthFragment;
            case 3:
                PodcastListFragment businessFragment = new PodcastListFragment();
                Bundle bussB = new Bundle();
                bussB.putString(AppConstants.INSTANCE.getCATEGORY_KEY(), AppConstants.INSTANCE.getBUSINESS_GENRE());
                businessFragment.setArguments(bussB);
                return businessFragment;
            case 4:
                PodcastListFragment sportsFragment = new PodcastListFragment();
                Bundle sportB = new Bundle();
                sportB.putString(AppConstants.INSTANCE.getCATEGORY_KEY(), AppConstants.INSTANCE.getSPORTS_GENRE());
                sportsFragment.setArguments(sportB);
                return sportsFragment;
        }
        return null;
    }

    public void refresh(List<String> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
