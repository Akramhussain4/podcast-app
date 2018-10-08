package com.hussain.podcastapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.hussain.podcastapp.R;
import com.hussain.podcastapp.service.AudioPlayerService;
import com.hussain.podcastapp.ui.PlayerActivity;
import com.hussain.podcastapp.utils.AppConstants;

public class PlayerWidget extends AppWidgetProvider {

    public static final String WIDGET_EPISODE_TITLE_INTENT_EXTRA = "widget-episode-title-intent-extra";
    public static final String WIDGET_IS_PLAYING_INTENT_EXTRA = "widget-is-playing-intent-extra";
    public static final String WIDGET_THUMBNAIL_URL_INTENT_EXTRA = "widget-thumbnail-url-intent-extra";
    public static final String WIDGET_NO_EPISODE_PLAYING_INTENT_EXTRA = "widget-no-episode-playing-intent-extra";

    public static final String PLAY_BUTTON_CLICKED_ACTION = "com.example.vidbregar.bluepodcast.PLAY_BUTTON_CLICKED_ACTION";
    public static final String PAUSE_BUTTON_CLICKED_ACTION = "com.example.vidbregar.bluepodcast.PAUSE_BUTTON_CLICKED_ACTION";
    private String episodeTitle;
    private String thumbnailUrl;
    private boolean isPlaying;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.player_widget);
        Intent intent = new Intent(context, PlayerActivity.class);
        PendingIntent playerPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.root_view, playerPendingIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private PendingIntent getPendingSelfIntent(Context context, String action) {
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

            if (intent.hasExtra(WIDGET_NO_EPISODE_PLAYING_INTENT_EXTRA)) {
                this.episodeTitle = null;
                this.isPlaying = false;
            } else if (intent.hasExtra(WIDGET_EPISODE_TITLE_INTENT_EXTRA) && intent.hasExtra(WIDGET_IS_PLAYING_INTENT_EXTRA)) {
                this.episodeTitle = intent.getStringExtra(WIDGET_EPISODE_TITLE_INTENT_EXTRA);
                this.thumbnailUrl = intent.getStringExtra(WIDGET_THUMBNAIL_URL_INTENT_EXTRA);
                this.isPlaying = intent.getBooleanExtra(WIDGET_IS_PLAYING_INTENT_EXTRA, false);
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

