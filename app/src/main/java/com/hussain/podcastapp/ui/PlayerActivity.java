package com.hussain.podcastapp.ui;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.hussain.podcastapp.R;
import com.hussain.podcastapp.base.BaseActivity;
import com.hussain.podcastapp.model.Item;
import com.hussain.podcastapp.utils.AppConstants;
import com.hussain.podcastapp.utils.GlideApp;

import butterknife.BindView;

public class PlayerActivity extends BaseActivity {

    private static final DefaultBandwidthMeter BANDWIDTH_METER =
            new DefaultBandwidthMeter();
    @BindView(R.id.video_view)
    PlayerView mPlayerView;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.tvSummary)
    TextView mTvSummary;
    @BindView(R.id.ivThumbnail)
    ImageView mIvThumb;
    private SimpleExoPlayer player;
    private String mURL, mTitle, mSummary, mImage;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onCreate(savedInstanceState, R.layout.activity_player);
        Bundle b = getIntent().getBundleExtra(AppConstants.BUNDLE_KEY);
        if (b != null) {
            Item item = b.getParcelable(AppConstants.ITEM_KEY);
            mURL = item.getUrl();
            mImage = item.getImage();
            mTitle = item.getTitle();
            mSummary = item.getSummary();
        }
    }

    private void initializePlayer() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(), new DefaultLoadControl());

            mPlayerView.setPlayer(player);
            mPlayerView.setControllerHideOnTouch(false);
            mPlayerView.setControllerShowTimeoutMs(10800000);
            player.setPlayWhenReady(true);
            Uri uri = Uri.parse(mURL);
            MediaSource mediaSource = buildMediaSource(uri);
            player.prepare(mediaSource, true, false);
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory("podplay")).
                createMediaSource(uri);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
            setUI();
        }
    }

    private void setUI() {
        mTvTitle.setText(mTitle);
        mTvSummary.setText(mSummary);
        GlideApp.with(this)
                .load(mImage)
                .placeholder(R.drawable.ic_launcher_foreground)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mIvThumb);
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            long playbackPosition = player.getCurrentPosition();
            long currentWindow = player.getCurrentWindowIndex();
            boolean playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    @Override
    public void onToolBarSetUp(Toolbar toolbar, ActionBar actionBar) {
        TextView tvHeader = toolbar.findViewById(R.id.tvClassName);
        tvHeader.setText("Pod play");
    }
}
