package com.hussain.podcastapp.ui;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;
import com.hussain.podcastapp.Application;
import com.hussain.podcastapp.R;
import com.hussain.podcastapp.base.BaseActivity;
import com.hussain.podcastapp.model.Item;
import com.hussain.podcastapp.service.AudioPlayerService;
import com.hussain.podcastapp.service.DownloadTracker;
import com.hussain.podcastapp.utils.AppConstants;
import com.hussain.podcastapp.utils.GlideApp;

import butterknife.BindView;

public class PlayerActivity extends BaseActivity implements DownloadTracker.Listener {

    @BindView(R.id.video_view)
    PlayerView mPlayerView;
    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.tvSummary)
    TextView mTvSummary;
    @BindView(R.id.ivThumbnail)
    ImageView mIvThumb;
    private String mUrl, mTitle, mSummary, mImage;
    private AudioPlayerService mService;
    private Intent intent;
    private String shareableLink;
    private boolean mBound = false;
    private DownloadTracker downloadTracker;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            AudioPlayerService.LocalBinder binder = (AudioPlayerService.LocalBinder) iBinder;
            mService = binder.getService();
            mBound = true;
            initializePlayer();
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
            shareableLink = b.getString(AppConstants.SHARE_KEY);
            mImage = item.getImage();
            mUrl = item.getUrl();
            mTitle = item.getTitle();
            mSummary = item.getSummary();
            intent = new Intent(this, AudioPlayerService.class);
            Bundle serviceBundle = new Bundle();
            serviceBundle.putParcelable(AppConstants.ITEM_KEY, item);
            intent.putExtra(AppConstants.BUNDLE_KEY, serviceBundle);
            Util.startForegroundService(this, intent);
            mPlayerView.setUseController(true);
            mPlayerView.showController();
            mPlayerView.setControllerAutoShow(true);
            mPlayerView.setControllerHideOnTouch(false);
        }
        downloadTracker = ((Application) getApplication()).getDownloadTracker();
    }

    private void initializePlayer() {
        if (mBound) {
            SimpleExoPlayer player = mService.getplayerInstance();
            mPlayerView.setPlayer(player);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        downloadTracker.addListener(this);
        initializePlayer();
        setUI();
    }

    private void setUI() {
        mTvTitle.setText(mTitle);
        mTvSummary.setText(Html.fromHtml(mSummary));
        GlideApp.with(this)
                .load(mImage)
                .placeholder(R.color.colorPrimary)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mIvThumb);
    }

    @Override
    protected void onStop() {
        unbindService(mConnection);
        downloadTracker.removeListener(this);
        mBound = false;
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
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
                shareIntent.putExtra(Intent.EXTRA_TEXT, mTitle + "\n\n" + shareableLink);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_text)));
                return true;
            case R.id.download_podcast:
                Uri uri = Uri.parse(mUrl);
                downloadTracker.toggleDownload(this, mTitle, uri, ".mp3");
//                ProgressiveDownloadAction progressiveDownloadAction
//                        = new ProgressiveDownloadAction(uri, false, null, null);
//                AudioDownloadService.startWithAction(PlayerActivity.this, AudioDownloadService.class,
//                        progressiveDownloadAction, false);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onToolBarSetUp(Toolbar toolbar, ActionBar actionBar) {
        TextView tvHeader = toolbar.findViewById(R.id.tvClassName);
        tvHeader.setText(R.string.app_name);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
    }

    @Override
    public void onDownloadsChanged() {
    }
}
