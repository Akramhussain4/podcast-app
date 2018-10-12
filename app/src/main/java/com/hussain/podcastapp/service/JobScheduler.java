package com.hussain.podcastapp.service;

import android.content.Context;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

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
                .setService(NotificationService.class)
                .setLifetime(Lifetime.FOREVER)
                .setReplaceCurrent(false)
                .setTag(jobTag)
                .setTrigger(Trigger.executionWindow(0, 1440))
                .setRecurring(true)
                .build();
        jobDispatcher.mustSchedule(syncJob);
        Log.d(TAG, "Job Scheduled ");
    }
}
