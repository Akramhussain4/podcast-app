package com.hussain.podcastapp.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class SharedPrefUtil(context: Context) {

    private val mSharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var isRegistered: Boolean
        get() = mSharedPref.getBoolean(IS_REGISTERED, false)
        set(isRegistered) = mSharedPref.edit()
                .putBoolean(IS_REGISTERED, isRegistered)
                .apply()

    fun setSeekPosition(feedId: String, position: Long) {
        mSharedPref.edit()
                .putLong(feedId, position)
                .apply()
    }

    fun getSeekPosition(feedId: String): Long {
        return mSharedPref.getLong(feedId, 0)
    }

    companion object {

        private const val IS_REGISTERED = "is_Registered"
    }

}
