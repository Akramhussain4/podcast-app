package com.hussain.podcastapp.database;

import com.hussain.podcastapp.model.ApiResponse;
import com.hussain.podcastapp.model.LookUpResponse;
import com.hussain.podcastapp.model.RssFeed;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("/us/rss/toppodcasts/limit=50/explicit=true/json")
    Call<ApiResponse> getTopPodcasts();

    @GET("us/rss/toppodcasts/genre={id}/json")
    Call<ApiResponse> getPodcastsByGengre(@Path("id") String genreID);

    @GET("/lookup")
    Call<LookUpResponse> getPlaylist(@Query("id") String id);

    @GET(".")
    Call<RssFeed> getRssFeed(@Query("id") String id);
}
