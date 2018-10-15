package com.hussain.podcastapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

import com.bumptech.glide.request.target.AppWidgetTarget;
import com.bumptech.glide.request.transition.Transition;
import com.hussain.podcastapp.R;
import com.hussain.podcastapp.model.Item;
import com.hussain.podcastapp.service.AudioPlayerService;
import com.hussain.podcastapp.ui.PlayerActivity;
import com.hussain.podcastapp.utils.AppConstants;
import com.hussain.podcastapp.utils.GlideApp;

import androidx.annotation.NonNull;

public class PlayerWidget extends AppWidgetProvider {

    public static final String WIDGET_PLAYING_EXTRA = "widget-is-playing";
    public static final String WIDGET_NO_PLAYING_EXTRA = "widget-no-episode-playing";

    public static final String PLAY_BUTTON_CLICKED_ACTION = "com.hussain.podcastapp.PLAY_BUTTON_CLICKED_ACTION";
    public static final String PAUSE_BUTTON_CLICKED_ACTION = "com.hussain.podcastapp.PAUSE_BUTTON_CLICKED_ACTION";

    private Item item;
    private String mTitle;
    private String mThumbnail;
    private boolean isPlaying;

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.player_widget);
        Intent intent = new Intent(context, PlayerActivity.class);
        Bundle serviceBundle = new Bundle();
        serviceBundle.putParcelable(AppConstants.ITEM_KEY, item);
        intent.putExtra(AppConstants.BUNDLE_KEY, serviceBundle);
        PendingIntent playerPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_root, playerPendingIntent);
        // Instruct the widget manager to update the widget
        if (mTitle == null) {
            views.setViewVisibility(R.id.podcast_title, View.GONE);
            views.setViewVisibility(R.id.widget_play, View.GONE);
            views.setViewVisibility(R.id.widget_pause, View.GONE);
            views.setViewVisibility(R.id.widget_no_playing, View.VISIBLE);
        } else {
            views.setViewVisibility(R.id.widget_no_playing, View.GONE);
            views.setViewVisibility(R.id.widget_title, View.VISIBLE);
            views.setTextViewText(R.id.widget_title, mTitle);
            views.setViewVisibility(R.id.widget_play, View.VISIBLE);
            views.setViewVisibility(R.id.widget_pause, View.VISIBLE);
            if (mThumbnail != null) {
                AppWidgetTarget appWidgetTarget = new AppWidgetTarget(context, R.id.widget_thumbnail, views, appWidgetId) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                        super.onResourceReady(resource, transition);
                    }
                };
                GlideApp.with(context)
                        .asBitmap()
                        .load(mThumbnail)
                        .into(appWidgetTarget);
            }
            if (isPlaying) {
                views.setViewVisibility(R.id.widget_play, View.INVISIBLE);
                views.setViewVisibility(R.id.widget_pause, View.VISIBLE);
                views.setOnClickPendingIntent(R.id.widget_pause,
                        getPendingIntent(context, PAUSE_BUTTON_CLICKED_ACTION));
            } else {
                views.setViewVisibility(R.id.widget_pause, View.INVISIBLE);
                views.setViewVisibility(R.id.widget_play, View.VISIBLE);
                views.setOnClickPendingIntent(R.id.widget_play,
                        getPendingIntent(context, PLAY_BUTTON_CLICKED_ACTION));
            }
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private PendingIntent getPendingIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action != null && action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

            if (intent.hasExtra(WIDGET_NO_PLAYING_EXTRA)) {
                this.mTitle = null;
                this.isPlaying = false;
            } else if (intent.hasExtra(AppConstants.BUNDLE_KEY) && intent.hasExtra(WIDGET_PLAYING_EXTRA)) {
                Bundle b = intent.getBundleExtra(AppConstants.BUNDLE_KEY);
                if (b != null) {
                    item = b.getParcelable(AppConstants.ITEM_KEY);
                    this.mTitle = item.getTitle();
                    this.mThumbnail = item.getImage();
                }
                this.isPlaying = intent.getBooleanExtra(WIDGET_PLAYING_EXTRA, false);
            }
            for (int appWidgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId);
            }
        }
        if (action.equals(PLAY_BUTTON_CLICKED_ACTION)) {
            Intent playEpisodeIntent = new Intent(context, AudioPlayerService.class);
            playEpisodeIntent.setAction(AppConstants.ACTION_PLAY);
            context.startService(playEpisodeIntent);
        }
        if (action.equals(PAUSE_BUTTON_CLICKED_ACTION)) {
            Intent pauseEpisodeIntent = new Intent(context, AudioPlayerService.class);
            pauseEpisodeIntent.setAction(AppConstants.ACTION_PAUSE);
            context.startService(pauseEpisodeIntent);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

