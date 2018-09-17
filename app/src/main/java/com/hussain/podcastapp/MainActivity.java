package com.hussain.podcastapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hussain.podcastapp.database.ApiInterface;
import com.hussain.podcastapp.model.ApiResponse;
import com.hussain.podcastapp.model.Entry;
import com.hussain.podcastapp.model.LookUpResponse;
import com.hussain.podcastapp.model.RssFeed;
import com.hussain.podcastapp.utils.AppConstants;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {


    private ApiResponse data;
    private String id;
    private String feedUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<ApiResponse> call = apiInterface.getTopPodcasts();
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                if (response != null) {
                    data = response.body();
                    List<Entry> entry = data.getFeed().getEntry();
                     id = entry.get(0).getFeedId().getAttributes().getIm();
                    ApiResponse.Feed dataFeed = data.getFeed();
                    lookUpCall();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.d("-----", t.getMessage());
            }
        });
    }

    private void lookUpCall() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<LookUpResponse> call1 = apiInterface.getPlaylist(id);
        call1.enqueue(new Callback<LookUpResponse>() {
            @Override
            public void onResponse(@NonNull Call<LookUpResponse> call, @NonNull Response<LookUpResponse> response) {
                if (response != null) {
                    LookUpResponse data = response.body();
                    feedUrl = data.getResults().get(0).getFeedUrl();
                    int i = feedUrl.length();
                    RssFeed(feedUrl);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LookUpResponse> call, @NonNull Throwable t) {
                Log.d("-----", t.getMessage());
            }
        });
    }

    private void RssFeed(String url){

        url  = url +"/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(new OkHttpClient())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<RssFeed> rssFeedCall = apiInterface.getRssFeed();
        rssFeedCall.enqueue(new Callback<RssFeed>() {
            @Override
            public void onResponse(Call<RssFeed> call, Response<RssFeed> response) {
                if (response != null) {
                    RssFeed data = response.body();
                    feedUrl = data.toString();
                    int i = feedUrl.length();
                }
            }

            @Override
            public void onFailure(Call<RssFeed> call, Throwable t) {
                Log.d("-----", t.getMessage());
            }
        });
    }
}
