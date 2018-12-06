package com.hussain.podcastapp

import android.app.Application

import com.crashlytics.android.Crashlytics

import io.fabric.sdk.android.Fabric

class PodPlayApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics())
    }
}
