package com.hussain.podcastapp.utils

import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors private constructor(val diskIO: Executor) {
    companion object {

        private val LOCK = Any()
        private var sInstance: AppExecutors? = null

        val instance: AppExecutors?
            get() {
                if (sInstance == null) {
                    synchronized(LOCK) {
                        sInstance = AppExecutors(Executors.newSingleThreadExecutor())
                    }
                }
                return sInstance
            }
    }
}
