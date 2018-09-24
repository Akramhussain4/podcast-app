package com.hussain.podcastapp.ui;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;
import com.hussain.podcastapp.R;
import com.hussain.podcastapp.base.BaseActivity;
import com.hussain.podcastapp.model.Item;
import com.hussain.podcastapp.service.AudioPlayerService;
import com.hussain.podcastapp.utils.AppConstants;
import com.hussain.podcastapp.utils.GlideApp;

import butterknife.BindView;

public class PlayerActivity extends BaseActivity {

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
    private AudioPlayerService mService;
    private boolean mBound = false;
    private Intent intent;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            AudioPlayerService.LocalBinder binder = (AudioPlayerService.LocalBinder) iBinder;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

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
            intent = new Intent(this, AudioPlayerService.class);
            Bundle serviceBundle = new Bundle();
            serviceBundle.putParcelable(AppConstants.ITEM_KEY, item);
            intent.putExtra(AppConstants.BUNDLE_KEY, serviceBundle);
            Util.startForegroundService(this, intent);

        }
    }

    private void initializePlayer() {
        if (player == null && !mURL.isEmpty() && mBound) {
            player = mService.getplayerInstance();
            mPlayerView.setPlayer(player);
            mPlayerView.setControllerHideOnTouch(false);
            mPlayerView.setControllerShowTimeoutMs(10800000);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        initializePlayer();
        setUI();
    }

    private void setUI() {
        mTvTitle.setText(mTitle);
        mTvSummary.setText(mSummary);
        GlideApp.with(this)
                .load(mImage)
                .placeholder(R.color.colorPrimary)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mIvThumb);
    }

    @Override
    protected void onStop() {
        unbindService(mConnection);
        mBound = false;
        releasePlayer();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.player_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_podcast:
                //Logic for Share
                return true;
            case R.id.download_podcast:
                //Logic for download
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onToolBarSetUp(Toolbar toolbar, ActionBar actionBar) {
        TextView tvHeader = toolbar.findViewById(R.id.tvClassName);
        tvHeader.setText(R.string.app_name);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
    }
}
