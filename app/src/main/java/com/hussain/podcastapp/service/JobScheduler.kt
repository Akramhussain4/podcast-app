package com.hussain.podcastapp.service

import android.content.Context
import android.util.Log
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.Lifetime
import com.firebase.jobdispatcher.Trigger
import java.util.concurrent.TimeUnit

class JobScheduler private constructor() {

    private val periodicity = TimeUnit.HOURS.toSeconds(12).toInt()
    private val toleranceInterval = TimeUnit.HOURS.toSeconds(1).toInt()

    fun scheduleJob(jobTag: String, context: Context) {
        val jobDispatcher = FirebaseJobDispatcher(GooglePlayDriver(context))
        val syncJob = jobDispatcher.newJobBuilder()
                .setService(NotificationService::class.java)
                .setLifetime(Lifetime.FOREVER)
                .setReplaceCurrent(false)
                .setTag(jobTag)
                .setTrigger(Trigger.executionWindow(periodicity, periodicity + toleranceInterval))
                .setRecurring(true)
                .build()
        jobDispatcher.mustSchedule(syncJob)
        Log.d(TAG, "Job Scheduled ")
    }

    companion object {

        private val TAG = JobScheduler::class.java.simpleName
        private var instance: JobScheduler? = null

        fun getInstance(): JobScheduler? {
            if (instance == null) {
                instance = JobScheduler()
            }
            return instance
        }
    }
}
