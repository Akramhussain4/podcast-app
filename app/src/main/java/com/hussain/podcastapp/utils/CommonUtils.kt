/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hussain.podcastapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log

import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache

import java.io.File

object CommonUtils {

    private val TAG = CommonUtils::class.java.name

    private var cache: Cache? = null

    @Synchronized
    fun getCache(context: Context): Cache? {
        if (cache == null) {
            val cacheDirectory = File(context.getExternalFilesDir(null), "pods")
            cache = SimpleCache(cacheDirectory, NoOpCacheEvictor())
        }
        return cache
    }

    fun isNetworkAvailable(context: Context): Boolean {
        var isNetwork = false
        try {
            val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var networkInfo: NetworkInfo? = null
            if (connMgr != null) {
                networkInfo = connMgr.activeNetworkInfo
            }
            if (networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected) {
                isNetwork = true
            }
        } catch (e: Exception) {
            Log.d(TAG, e.message, e)
        }

        return isNetwork
    }

}
