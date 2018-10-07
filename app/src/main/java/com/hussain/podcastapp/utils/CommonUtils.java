package com.hussain.podcastapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class CommonUtils {

    private static final String TAG = CommonUtils.class.getName();

    public static boolean isNetworkAvailable(Context context) {
        boolean isNetwork = false;
        try {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (connMgr != null) {
                networkInfo = connMgr.getActiveNetworkInfo();
            }
            if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
                isNetwork = true;
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage(), e);
        }

        return isNetwork;
    }
}
