package com.hussain.podcastapp.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.hussain.podcastapp.R;
import com.hussain.podcastapp.model.Item;
import com.hussain.podcastapp.ui.PlayerActivity;
import com.hussain.podcastapp.utils.AppConstants;
import com.hussain.podcastapp.utils.CommonUtils;
import com.hussain.podcastapp.widget.PlayerWidget;

import static com.hussain.podcastapp.utils.AppConstants.ACTION_PAUSE;
import static com.hussain.podcastapp.utils.AppConstants.ACTION_PLAY;

public class AudioPlayerService extends Service implements Player.EventListener {

    private final IBinder mBinder = new LocalBinder();
    private SimpleExoPlayer mPlayer;
    private Item mItem;
    private PlayerNotificationManager mPlayerNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayerNotificationManager.setPlayer(null);
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public SimpleExoPlayer getPlayerInstance() {
        if (mItem != null && mPlayer == null && !TextUtils.isEmpty(mItem.getUrl())) {
            startPlayer();
        }
        return mPlayer;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle b = intent.getBundleExtra(AppConstants.BUNDLE_KEY);
        if (b != null) {
            releasePlayer();
            mItem = b.getParcelable(AppConstants.ITEM_KEY);
        }
        if (mItem != null && mPlayer == null && !TextUtils.isEmpty(mItem.getUrl())) {
            startPlayer();
        }
        String action = intent.getAction();
        if (mPlayer != null) {
            if (!TextUtils.isEmpty(action) && action.equalsIgnoreCase(ACTION_PLAY)) {
                mPlayer.setPlayWhenReady(true);

            }
            if (!TextUtils.isEmpty(action) && action.equalsIgnoreCase(ACTION_PAUSE)) {
                mPlayer.setPlayWhenReady(false);
            }
        }
        return START_STICKY;
    }

    private void startPlayer() {
        final Context context = this;
        Uri uri = Uri.parse(mItem.getUrl());
        mPlayer = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, getString(R.string.app_name)));
        CacheDataSourceFactory cacheDataSourceFactory = new CacheDataSourceFactory(
                CommonUtils.getCache(context),
                dataSourceFactory,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
        MediaSource mediaSource = new ExtractorMediaSource.Factory(cacheDataSourceFactory)
                .createMediaSource(uri);
        mPlayer.prepare(mediaSource);
        mPlayer.addListener(this);
        mPlayer.setPlayWhenReady(true);
        mPlayerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(context, AppConstants.PLAYBACK_CHANNEL_ID,
                R.string.playback_channel_name,
                AppConstants.PLAYBACK_NOTIFICATION_ID,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public String getCurrentContentTitle(Player player) {
                        return mItem.getTitle();
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        Intent intent = new Intent(context, PlayerActivity.class);
                        Bundle serviceBundle = new Bundle();
                        serviceBundle.putParcelable(AppConstants.ITEM_KEY, mItem);
                        intent.putExtra(AppConstants.BUNDLE_KEY, serviceBundle);
                        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    @Nullable
                    @Override
                    public String getCurrentContentText(Player player) {
                        return mItem.getSummary();
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                        return null;
                    }
                }
        );
        mPlayerNotificationManager.setNotificationListener(new PlayerNotificationManager.NotificationListener() {
            @Override
            public void onNotificationStarted(int notificationId, Notification notification) {
                startForeground(notificationId, notification);
            }

            @Override
            public void onNotificationCancelled(int notificationId) {
                Intent intent = new Intent(context, PlayerWidget.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                int[] ids = AppWidgetManager.getInstance(getApplication())
                        .getAppWidgetIds(new ComponentName(getApplication(), PlayerWidget.class));
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                intent.putExtra(PlayerWidget.WIDGET_NO_PLAYING_EXTRA, "");
                sendBroadcast(intent);
                stopSelf();
            }
        });
        mPlayerNotificationManager.setPlayer(mPlayer);
    }

    private void updateWidget(boolean playWhenReady) {
        Intent intent = new Intent(this, PlayerWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), PlayerWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        Bundle serviceBundle = new Bundle();
        serviceBundle.putParcelable(AppConstants.ITEM_KEY, mItem);
        intent.putExtra(AppConstants.BUNDLE_KEY, serviceBundle);
        intent.putExtra(PlayerWidget.WIDGET_PLAYING_EXTRA, playWhenReady);
        sendBroadcast(intent);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        updateWidget(playWhenReady);
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    public class LocalBinder extends Binder {
        public AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }
}
