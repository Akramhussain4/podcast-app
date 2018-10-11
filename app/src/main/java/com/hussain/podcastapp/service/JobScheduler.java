package com.hussain.podcastapp.service;

import android.content.Context;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;

public class JobScheduler {

    private static String TAG = JobScheduler.class.getSimpleName();
    private static JobScheduler instance = null;

    private JobScheduler() {
    }

    public static JobScheduler getInstance() {
        if (instance == null) {
            instance = new JobScheduler();
        }
        return instance;
    }

    public void scheduleJob(String jobTag, Context context) {
        FirebaseJobDispatcher jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job syncJob = jobDispatcher.newJobBuilder()
                .setService(NetworkConnectivity.class)
                .setTag(jobTag)
                .setLifetime(Lifetime.FOREVER)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();
        jobDispatcher.mustSchedule(syncJob);
        //   int schedule = jobDispatcher.schedule(syncJob);

        Log.d(TAG, "Job Scheduled ");
    }
}
