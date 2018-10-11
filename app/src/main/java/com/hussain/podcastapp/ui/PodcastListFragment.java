package com.hussain.podcastapp.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hussain.podcastapp.R;
import com.hussain.podcastapp.adapter.PodcastAdapter;
import com.hussain.podcastapp.base.IBaseView;
import com.hussain.podcastapp.customview.CustomBottomSheet;
import com.hussain.podcastapp.customview.GridAutofitLayoutManager;
import com.hussain.podcastapp.database.ApiInterface;
import com.hussain.podcastapp.database.AppDatabase;
import com.hussain.podcastapp.model.ApiResponse;
import com.hussain.podcastapp.model.Entry;
import com.hussain.podcastapp.model.LookUpResponse;
import com.hussain.podcastapp.utils.AppConstants;
import com.hussain.podcastapp.utils.AppExecutors;

import java.util.List;
import java.util.Map;

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
    private PodcastAdapter mAdapter;
    private String mCategory;
    private LookUpResponse.Results mResults;
    private MainActivity mActivityContext;
    private DatabaseReference mDatabase;
    private BottomSheetDialog mBottomDialog;
    private AppDatabase mDb;
    private boolean isClicked = true;
    private boolean mSubscribed = false;
    private String mUserId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            mCategory = b.getString(AppConstants.CATEGORY_KEY);
        }
        mDb = AppDatabase.getInstance(getContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mUserId = user.getUid();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_podcasts_list, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        mRecyclerView.setLayoutManager(new GridAutofitLayoutManager(getContext(), 300));
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
        mActivityContext = (MainActivity) getActivity();
        super.onActivityCreated(savedInstanceState);
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
                    mAdapter = new PodcastAdapter(mEntryData, mContext);
                    mRecyclerView.setAdapter(mAdapter);
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
            String id = item.getFeedId().getAttributes().getId();
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
                    checkIfSubscribed(item, mResults);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LookUpResponse> call, @NonNull Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    private void checkIfSubscribed(Entry item, LookUpResponse.Results mResults) {
        String feedId = item.getFeedId().getAttributes().getId();
        AppExecutors.getInstance().getDiskIO().execute(() -> {
            Entry entry = mDb.entryDao().getPodcast(feedId);
            if (entry != null) {
                mSubscribed = true;
                mActivityContext.runOnUiThread(() -> bottomDialog(item, mResults, true));
            } else {
                mActivityContext.runOnUiThread(() -> bottomDialog(item, mResults, false));
            }
        });
    }

    private void bottomDialog(Entry item, LookUpResponse.Results results, boolean sub) {
        mBottomDialog = CustomBottomSheet.showBottomDialog(getActivity(), item, results.getArtWork(), view -> {
            Intent intent = new Intent(getContext(), EpisodesActivity.class);
            intent.putExtra(AppConstants.FEED_URL_KEY, mResults.getFeedUrl());
            intent.putExtra(AppConstants.ARTWORK_URL, mResults.getArtWork());
            showBottomDialog(false);
            startActivity(intent);
        }, view -> {
            if (mSubscribed) {
                handleDelete(item, view);
            } else {
                handleInsert(item, view);
            }
        }, sub);
        showBottomDialog(true);
        isClicked = true;
        showAnimation(false);
    }

    private void handleDelete(Entry item, View view) {
        Button btSub = view.findViewById(R.id.btSubscribe);
        btSub.setText(R.string.subscribe);
        mSubscribed = false;
        AppExecutors.getInstance().getDiskIO().execute(() ->
                mDb.entryDao().deletePodcast(item.getFeedId().getAttributes().getId()));
        mDatabase.child(mUserId).child(item.getFeedId().getAttributes().getId()).removeValue();
    }

    private void handleInsert(Entry item, View view) {
        Button btSub = view.findViewById(R.id.btSubscribe);
        btSub.setText(R.string.unsubscribe);
        mSubscribed = true;
        AppExecutors.getInstance().getDiskIO().execute(() ->
                mDb.entryDao().insertPodcast(item));
        Map<String, Object> postValues = item.toMap();
        mDatabase.child(mUserId).child(item.getFeedId().attributes.id).setValue(postValues);
    }


    private void showBottomDialog(boolean show) {
        if (mBottomDialog != null) {
            if (show) {
                mBottomDialog.show();
            } else {
                mBottomDialog.dismiss();
            }
        }
    }

    @Override
    public void onStop() {
        showBottomDialog(false);
        super.onStop();
    }

    @Override
    public void showAnimation(boolean show) {
        if (mActivityContext != null) {
            mActivityContext.showAnimation(show);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity()
                .getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getActivity().getComponentName()));
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}