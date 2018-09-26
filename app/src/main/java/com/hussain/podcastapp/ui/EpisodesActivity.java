package com.hussain.podcastapp.ui;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hussain.podcastapp.R;
import com.hussain.podcastapp.adapter.EpisodeAdapter;
import com.hussain.podcastapp.base.BaseActivity;
import com.hussain.podcastapp.database.ApiInterface;
import com.hussain.podcastapp.model.Channel;
import com.hussain.podcastapp.model.Item;
import com.hussain.podcastapp.model.RssFeed;
import com.hussain.podcastapp.utils.AppConstants;
import com.hussain.podcastapp.utils.GlideApp;

import java.util.List;

import butterknife.BindView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class EpisodesActivity extends BaseActivity implements EpisodeAdapter.EpisodeClickListener {

    private static final String TAG = EpisodesActivity.class.getName();

    @BindView(R.id.tvTitle)
    TextView mTitle;
    @BindView(R.id.tvDescription)
    TextView mDescription;
    @BindView(R.id.ivArtwork)
    ImageView mArtwork;
    @BindView(R.id.tvEpisodes)
    TextView mTvEpispodes;
    @BindView(R.id.rvEpisodes)
    RecyclerView rvEpisodes;
    @BindView(R.id.tvError)
    TextView mTvError;
    @BindView(R.id.error_layout)
    ConstraintLayout mErrorLayout;
    @Nullable
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mEpisodeLayout;

    private String mArtworkUrl;
    private List<Item> mItems;
    private Channel mChannel;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onCreate(savedInstanceState, R.layout.activity_episodes);
        Intent b = getIntent();
        String mFeedUrl = b.getStringExtra(AppConstants.FEED_URL_KEY);
        mArtworkUrl = b.getStringExtra(AppConstants.ARTWORK_URL);
        if (mFeedUrl != null) {
            RssFeed(mFeedUrl);
            rvEpisodes.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void setUI() {
        if (mChannel != null && mItems != null) {
            String episodesText = mItems.size() + " " + getResources().getString(R.string.episodes_text);
            mTitle.setText(mChannel.getTitle());
            mDescription.setText(Html.fromHtml(mChannel.getDescription()));
            mTvEpispodes.setText(episodesText);
            GlideApp.with(this)
                    .load(mArtworkUrl)
                    .placeholder(R.color.colorPrimary)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mArtwork);
            EpisodeAdapter adapter = new EpisodeAdapter(mItems, this);
            rvEpisodes.setAdapter(adapter);
            showAnimation(false);
        }
    }

    private void RssFeed(String url) {
        showAnimation(true);
        Uri uri = Uri.parse(url);
        String protocol = uri.getScheme();
        String server = uri.getAuthority();
        String path = uri.getPath();
        String limit = uri.getQueryParameter("id");
        url = protocol + "://" + server + path + "/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(new OkHttpClient())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<RssFeed> rssFeedCall = apiInterface.getRssFeed(limit);
        rssFeedCall.enqueue(new Callback<RssFeed>() {
            @Override
            public void onResponse(@NonNull Call<RssFeed> call, @NonNull Response<RssFeed> response) {
                RssFeed data = response.body();
                if (data != null) {
                    mChannel = data.getChannel();
                    mItems = mChannel.getItems();
                    setUI();
                } else {
                    mEpisodeLayout.setVisibility(View.GONE);
                    mErrorLayout.setVisibility(View.VISIBLE);
                    mTvError.setText(R.string.no_podcast);
                    showAnimation(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<RssFeed> call, @NonNull Throwable t) {
                Log.d(TAG, t.getMessage());
                Toast.makeText(getApplicationContext(), R.string.user_error_text, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    public void onEpisodeClick(Item item, int position, ImageView view) {
        Intent intent = new Intent(this, PlayerActivity.class);
        Bundle dataBundle = new Bundle();
        dataBundle.putParcelable(AppConstants.ITEM_KEY, item);
        dataBundle.putString(AppConstants.SHARE_KEY, mChannel.getShareLink());
        intent.putExtra(AppConstants.BUNDLE_KEY, dataBundle);
        Bundle transitionBundle = ActivityOptions.makeSceneTransitionAnimation(this, view, view.getTransitionName()).toBundle();
        startActivity(intent, transitionBundle);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(AppConstants.SCROLL_POSITION, rvEpisodes.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        rvEpisodes.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(AppConstants.SCROLL_POSITION));
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onToolBarSetUp(Toolbar toolbar, ActionBar actionBar) {
        TextView tvHeader = toolbar.findViewById(R.id.tvClassName);
        tvHeader.setText(R.string.app_name);
    }
}

