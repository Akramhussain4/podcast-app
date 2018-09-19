package com.hussain.podcastapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hussain.podcastapp.R;
import com.hussain.podcastapp.adapter.EpisodeAdapter;
import com.hussain.podcastapp.database.ApiInterface;
import com.hussain.podcastapp.model.Channel;
import com.hussain.podcastapp.model.Item;
import com.hussain.podcastapp.model.RssFeed;
import com.hussain.podcastapp.utils.AppConstants;
import com.hussain.podcastapp.utils.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class EpisodesActivity extends AppCompatActivity implements EpisodeAdapter.EpisodeClickListener {

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

    private String mArtworkUrl;
    private List<Item> mItems;
    private Channel mChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes);
        ButterKnife.bind(this);
        Intent b = getIntent();
        if (b != null) {
            String mFeedUrl = b.getStringExtra(AppConstants.FEED_URL_KEY);
            mArtworkUrl = b.getStringExtra(AppConstants.ARTWORK_URL);
            RssFeed(mFeedUrl);
        }
    }

    private void setUI() {
        if (mChannel != null && mItems != null) {
            String episodesText = mItems.size() + getResources().getString(R.string.episodes_text);
            mTitle.setText(mChannel.getTitle());
            mDescription.setText(Html.fromHtml(mChannel.getDescription()));
            mTvEpispodes.setText(episodesText);
            GlideApp.with(this)
                    .load(mArtworkUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mArtwork);
            EpisodeAdapter adapter = new EpisodeAdapter(mItems,this);
            rvEpisodes.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            rvEpisodes.setAdapter(adapter);
        }
    }

    private void RssFeed(String url) {
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
                    Toast.makeText(getApplicationContext(), "SERVER DOWN FOR THIS PODCAST!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RssFeed> call, @NonNull Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    @Override
    public void onEpisodeClick(Item item, int position) {

    }
}

