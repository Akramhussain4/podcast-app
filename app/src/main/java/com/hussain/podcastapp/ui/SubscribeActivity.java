package com.hussain.podcastapp.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hussain.podcastapp.R;
import com.hussain.podcastapp.adapter.PodcastAdapter;
import com.hussain.podcastapp.base.BaseActivity;
import com.hussain.podcastapp.customview.GridAutofitLayoutManager;
import com.hussain.podcastapp.database.ApiInterface;
import com.hussain.podcastapp.database.AppDatabase;
import com.hussain.podcastapp.model.Entry;
import com.hussain.podcastapp.model.LookUpResponse;
import com.hussain.podcastapp.utils.AppConstants;
import com.hussain.podcastapp.utils.AppExecutors;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SubscribeActivity extends BaseActivity implements PodcastAdapter.PodcastClickListener {


    private static final String TAG = SubscribeActivity.class.getName();

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.error_layout)
    ConstraintLayout mErrorLayout;
    @BindView(R.id.tvError)
    TextView mTvError;

    private List<Entry> mEntryData;
    private List<Entry> mFirebaseData;
    private LookUpResponse.Results mResults;
    private AppDatabase mDb;
    private DatabaseReference mDatabase;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onCreate(savedInstanceState, R.layout.activity_subscribe);
        mDb = AppDatabase.getInstance(this);
        String mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(mUserId);
        mFirebaseData = new ArrayList<>();
        mRecyclerView.setLayoutManager(new GridAutofitLayoutManager(this, 300));
    }

    @Override
    protected void onStart() {
        super.onStart();
        networkCall();
        showAnimation(true);
    }

    private void networkCall() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    mFirebaseData.add(postSnapshot.getValue(Entry.class));
                }
                insertData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void insertData() {
        showAnimation(false);
        if (mFirebaseData != null && mFirebaseData.size() > 0) {
            AppExecutors.getInstance().getDiskIO().execute(() ->
                    mDb.entryDao().insertPodcastList(mFirebaseData));
            setUI();
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mErrorLayout.setVisibility(View.VISIBLE);
            mTvError.setText(R.string.no_subscriptions);
        }
    }

    private void setUI() {
        AppExecutors.getInstance().getDiskIO().execute(() -> {
            mEntryData = mDb.entryDao().loadAllPodcasts();
            if (mEntryData != null && mEntryData.size() > 0) {
                runOnUiThread(() -> {
                    PodcastAdapter podcastAdapter = new PodcastAdapter(mEntryData, SubscribeActivity.this);
                    mRecyclerView.setAdapter(podcastAdapter);
                });
            } else {
                runOnUiThread(() -> {
                    mRecyclerView.setVisibility(View.GONE);
                    mErrorLayout.setVisibility(View.VISIBLE);
                    mTvError.setText(R.string.no_subscriptions);
                });
            }
        });
    }

    @Override
    public void onToolBarSetUp(Toolbar toolbar, ActionBar actionBar) {
        TextView tvHeader = toolbar.findViewById(R.id.tvClassName);
        tvHeader.setText(R.string.subscriptions);
    }

    @Override
    public void onPodcastClick(Entry item, int position) {
        showAnimation(true);
        String id = item.getFeedId().getAttributes().getId();
        lookUpCall(id);
    }

    private void lookUpCall(String id) {
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
                    openEpisode(mResults);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LookUpResponse> call, @NonNull Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private void openEpisode(LookUpResponse.Results results) {
        showAnimation(false);
        Intent intent = new Intent(this, EpisodesActivity.class);
        intent.putExtra(AppConstants.FEED_URL_KEY, results.getFeedUrl());
        intent.putExtra(AppConstants.ARTWORK_URL, results.getArtWork());
        startActivity(intent);
    }
}
