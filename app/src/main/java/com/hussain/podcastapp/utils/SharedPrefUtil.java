package com.hussain.podcastapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefUtil {

    private static final String IS_REGISTERED = "is_Registered";

    private SharedPreferences mSharedPref;

    public SharedPrefUtil(Context context) {
        this.mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean getIsRegistered() {
        return mSharedPref.getBoolean(IS_REGISTERED, false);
    }

    public void setIsRegistered(boolean isRegistered) {
        mSharedPref.edit()
                .putBoolean(IS_REGISTERED, isRegistered)
                .apply();
    }

    public void setSeekPosition(String feedId, long position) {
        mSharedPref.edit()
                .putLong(feedId, position)
                .apply();
    }

    public long getSeekPosition(String feedId) {
        return mSharedPref.getLong(feedId, 0);
    }

}
