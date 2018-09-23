package com.hussain.podcastapp.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.hussain.podcastapp.model.Item;
import com.hussain.podcastapp.ui.PlayerActivity;
import com.hussain.podcastapp.utils.AppConstants;

public class AudioPlayerService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private SimpleExoPlayer player;
    private Item item;
    private PlayerNotificationManager playerNotificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        playerNotificationManager.setPlayer(null);
        player.release();
        player = null;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public SimpleExoPlayer getplayerInstance() {
        return player;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle b = intent.getBundleExtra(AppConstants.BUNDLE_KEY);
        if (b != null) {
            item = b.getParcelable(AppConstants.ITEM_KEY);
        }
        startPlayer();
        return START_STICKY;
    }

    @Override
    public void onRebind(Intent intent) {
        stopForeground(true);
        super.onRebind(intent);
    }

    private void startPlayer() {
        final Context context = this;
        Uri uri = Uri.parse(item.getUrl());
        player = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "podplay"));
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(context, "podplay", 0, 1,
                new PlayerNotificationManager.MediaDescriptionAdapter() {
                    @Override
                    public String getCurrentContentTitle(Player player) {
                        return item.getTitle();
                    }

                    @Nullable
                    @Override
                    public PendingIntent createCurrentContentIntent(Player player) {
                        Intent intent = new Intent(context, PlayerActivity.class);
                        Bundle serviceBundle = new Bundle();
                        serviceBundle.putParcelable(AppConstants.ITEM_KEY, item);
                        intent.putExtra(AppConstants.BUNDLE_KEY, serviceBundle);
                        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    }

                    @Nullable
                    @Override
                    public String getCurrentContentText(Player player) {
                        return item.getSummary();
                    }

                    @Nullable
                    @Override
                    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                        return item.getBitmap();
                    }
                }
        );
        playerNotificationManager.setNotificationListener(new PlayerNotificationManager.NotificationListener() {
            @Override
            public void onNotificationStarted(int notificationId, Notification notification) {
                startForeground(notificationId, notification);
            }

            @Override
            public void onNotificationCancelled(int notificationId) {
                stopSelf();
            }
        });
        playerNotificationManager.setPlayer(player);
    }

    public class LocalBinder extends Binder {
        public AudioPlayerService getService() {
            return AudioPlayerService.this;
        }
    }
}
