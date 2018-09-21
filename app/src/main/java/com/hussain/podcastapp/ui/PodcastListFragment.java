package com.hussain.podcastapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hussain.podcastapp.R;
import com.hussain.podcastapp.adapter.PodcastAdapter;
import com.hussain.podcastapp.base.IBaseView;
import com.hussain.podcastapp.customview.CustomBottomSheet;
import com.hussain.podcastapp.database.ApiInterface;
import com.hussain.podcastapp.model.ApiResponse;
import com.hussain.podcastapp.model.Entry;
import com.hussain.podcastapp.model.LookUpResponse;
import com.hussain.podcastapp.utils.AppConstants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PodcastListFragment extends Fragment implements PodcastAdapter.PodcastClickListener, IBaseView {

    private static final String TAG = PodcastListFragment.class.getName();

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout mSwipeRefresh;

    private List<Entry> mEntryData;
    private PodcastListFragment mContext;
    private String mCategory;
    private LookUpResponse.Results mResults;
    private MainActivity activityContext;
    private BottomSheetDialog bottomSheetDialog;
    private boolean isClicked = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            mCategory = b.getString(AppConstants.CATEGORY_KEY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_podcasts_list, container, false);
        ButterKnife.bind(this, view);
        mContext = this;
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        networkCall();
        mSwipeRefresh.setOnRefreshListener(this::networkCall);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activityContext = (MainActivity) getActivity();
    }

    private void networkCall() {
        showAnimation(true);
        int cacheSize = 10 * 1024 * 1024; // 10 MB
        Cache cache = new Cache(getActivity().getCacheDir(), cacheSize);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ApiResponse> call = apiInterface.getPodcastsByGengre(mCategory);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                ApiResponse mData = response.body();
                showAnimation(false);
                if (mData != null) {
                    mSwipeRefresh.setRefreshing(false);
                    mEntryData = mData.getFeed().getEntry();
                    PodcastAdapter podcastAdapter = new PodcastAdapter(mEntryData, mContext);
                    mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    mRecyclerView.setAdapter(podcastAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    @Override
    public void onPodcastClick(Entry item, int position) {
        if (isClicked) {
            isClicked = false;
            showAnimation(true);
            String id = item.getFeedId().getAttributes().getIm();
            lookUpCall(id, item);
        }
    }

    private void lookUpCall(String id, Entry item) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<LookUpResponse> call1 = apiInterface.getPlaylist(id);
        call1.enqueue(new Callback<LookUpResponse>() {
            @Override
            public void onResponse(@NonNull Call<LookUpResponse> call, @NonNull Response<LookUpResponse> response) {
                LookUpResponse data = response.body();
                if (data != null) {
                    mResults = data.getResults().get(0);
                    openBottomDialog(item, mResults);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LookUpResponse> call, @NonNull Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private void openBottomDialog(Entry item, LookUpResponse.Results results) {
        bottomSheetDialog = CustomBottomSheet.showBottomDialog(getActivity(), item, results.getArtWork(), view -> {
            Intent intent = new Intent(getContext(), EpisodesActivity.class);
            intent.putExtra(AppConstants.FEED_URL_KEY, mResults.getFeedUrl());
            intent.putExtra(AppConstants.ARTWORK_URL, mResults.getArtWork());
            showBottomDialog(false);
            startActivity(intent);
        }, view -> {
            //Logic for Subscribe
        });
        showBottomDialog(true);
        isClicked = true;
        showAnimation(false);
    }

    private void showBottomDialog(boolean show) {
        if (bottomSheetDialog != null) {
            if (show) {
                bottomSheetDialog.show();
            } else {
                bottomSheetDialog.dismiss();
            }
        }
    }

    @Override
    public void showAnimation(boolean show) {
        if (activityContext != null) {
            activityContext.showAnimation(show);
        }
    }
}