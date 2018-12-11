package com.hussain.podcastapp.database

import com.hussain.podcastapp.model.ApiResponse
import com.hussain.podcastapp.model.LookUpResponse
import com.hussain.podcastapp.model.RssFeed
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @get:GET("/us/rss/toppodcasts/limit=200/explicit=true/json")
    val topPodcasts: Call<ApiResponse>

    @GET("us/rss/toppodcasts/limit=200/genre={id}/json")
    fun getPodcastsByGengre(@Path("id") genreID: String): Call<ApiResponse>

    @GET("/lookup")
    fun getPlaylist(@Query("id") id: String): Call<LookUpResponse>

    @GET(".")
    fun getRssFeed(@Query("id") id: String): Call<RssFeed>
}
