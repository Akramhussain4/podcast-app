package com.hussain.podcastapp.service;

import android.content.Context;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class JobScheduler {

    private static String TAG = JobScheduler.class.getSimpleName();

    final int periodicity = (int) TimeUnit.HOURS.toSeconds(12);
    final int toleranceInterval = (int) TimeUnit.HOURS.toSeconds(1);
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
                .setTrigger(Trigger.executionWindow(periodicity, periodicity + toleranceInterval))
                .setRecurring(true)
                .build();
        jobDispatcher.mustSchedule(syncJob);
        Log.d(TAG, "Job Scheduled ");
    }
}
