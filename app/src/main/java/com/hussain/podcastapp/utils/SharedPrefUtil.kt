package com.hussain.podcastapp.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


class SharedPrefUtil(context: Context) {

    private val mSharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var isRegistered: Boolean = false


    fun setSeekPosition(feedId: String, position: Long) {
        mSharedPref.edit()
                .putLong(feedId, position)
                .apply()
    }

    fun getSeekPosition(feedId: String): Long {
        return mSharedPref.getLong(feedId, 0)
    }

    fun getIsRegistered(): Boolean {
        return mSharedPref.getBoolean(IS_REGISTERED, false)
    }

    fun setIsRegistered(isRegistered: Boolean) {
        mSharedPref.edit()
                .putBoolean(IS_REGISTERED, isRegistered)
                .apply()
    }
    companion object {

        private const val IS_REGISTERED = "is_Registered"
    }

}
