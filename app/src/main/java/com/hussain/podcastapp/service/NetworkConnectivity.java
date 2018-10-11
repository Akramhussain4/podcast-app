package com.hussain.podcastapp.service;

import android.content.Context;
import android.util.Log;

import com.firebase.jobdispatcher.JobService;
import com.hussain.podcastapp.utils.CommonUtils;

public class NetworkConnectivity extends JobService {

    private static final String TAG = NetworkConnectivity.class.getSimpleName();
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Log.d(TAG, "onCreate()");
    }

    @Override
    public boolean onStartJob(com.firebase.jobdispatcher.JobParameters job) {
        Log.d(TAG, "Job started");
        String message = CommonUtils.isNetworkAvailable(context) ? "Good! Connected to Internet" : "Sorry! Not connected to internet";
        Log.d(TAG, message);
        return true;
    }

    @Override
    public boolean onStopJob(com.firebase.jobdispatcher.JobParameters job) {
        Log.d(TAG, "Job ended");
        return true;
    }
}
