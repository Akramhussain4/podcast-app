package com.hussain.podcastapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import com.bumptech.glide.request.target.AppWidgetTarget
import com.hussain.podcastapp.R
import com.hussain.podcastapp.model.Item
import com.hussain.podcastapp.service.AudioPlayerService
import com.hussain.podcastapp.ui.PlayerActivity
import com.hussain.podcastapp.utils.AppConstants
import com.hussain.podcastapp.utils.GlideApp

class PlayerWidget : AppWidgetProvider() {

    private var item: Item? = null
    private var mTitle: String? = null
    private var mThumbnail: String? = null
    private var isPlaying: Boolean = false

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                appWidgetId: Int) {

        val views = RemoteViews(context.packageName, R.layout.player_widget)
        val intent = Intent(context, PlayerActivity::class.java)
        val serviceBundle = Bundle()
        serviceBundle.putParcelable(AppConstants.ITEM_KEY, item)
        intent.putExtra(AppConstants.BUNDLE_KEY, serviceBundle)
        val playerPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        views.setOnClickPendingIntent(R.id.widget_root, playerPendingIntent)
        // Instruct the widget manager to update the widget
        if (mTitle == null) {
            views.setViewVisibility(R.id.widget_thumbnail, View.INVISIBLE)
            views.setViewVisibility(R.id.widget_title, View.GONE)
            views.setViewVisibility(R.id.widget_play, View.GONE)
            views.setViewVisibility(R.id.widget_pause, View.GONE)
            views.setViewVisibility(R.id.widget_no_playing, View.VISIBLE)
        } else {
            views.setViewVisibility(R.id.widget_no_playing, View.GONE)
            views.setViewVisibility(R.id.widget_title, View.VISIBLE)
            views.setViewVisibility(R.id.widget_thumbnail, View.VISIBLE)
            views.setTextViewText(R.id.widget_title, mTitle)
            views.setViewVisibility(R.id.widget_play, View.VISIBLE)
            views.setViewVisibility(R.id.widget_pause, View.VISIBLE)
            if (mThumbnail != null) {
                val appWidgetTarget = object : AppWidgetTarget(context, R.id.widget_thumbnail, views, appWidgetId) {
                }
                GlideApp.with(context)
                        .asBitmap()
                        .load(mThumbnail)
                        .override(200, 200)
                        .into<AppWidgetTarget>(appWidgetTarget)
            }
            if (isPlaying) {
                views.setViewVisibility(R.id.widget_play, View.INVISIBLE)
                views.setViewVisibility(R.id.widget_pause, View.VISIBLE)
                views.setOnClickPendingIntent(R.id.widget_pause,
                        getPendingIntent(context, PAUSE_BUTTON_CLICKED_ACTION))
            } else {
                views.setViewVisibility(R.id.widget_pause, View.INVISIBLE)
                views.setViewVisibility(R.id.widget_play, View.VISIBLE)
                views.setOnClickPendingIntent(R.id.widget_play,
                        getPendingIntent(context, PLAY_BUTTON_CLICKED_ACTION))
            }
        }
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun getPendingIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, javaClass)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != null && action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)

            if (intent.hasExtra(WIDGET_NO_PLAYING_EXTRA)) {
                this.mTitle = null
                this.isPlaying = false
            } else if (intent.hasExtra(AppConstants.BUNDLE_KEY) && intent.hasExtra(WIDGET_PLAYING_EXTRA)) {
                val b = intent.getBundleExtra(AppConstants.BUNDLE_KEY)
                if (b != null) {
                    item = b.getParcelable(AppConstants.ITEM_KEY)
                    this.mTitle = item!!.title
                    this.mThumbnail = item!!.image
                }
                this.isPlaying = intent.getBooleanExtra(WIDGET_PLAYING_EXTRA, false)
            }
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
        if (action == PLAY_BUTTON_CLICKED_ACTION) {
            val playEpisodeIntent = Intent(context, AudioPlayerService::class.java)
            playEpisodeIntent.action = AppConstants.ACTION_PLAY
            context.startService(playEpisodeIntent)
        }
        if (action == PAUSE_BUTTON_CLICKED_ACTION) {
            val pauseEpisodeIntent = Intent(context, AudioPlayerService::class.java)
            pauseEpisodeIntent.action = AppConstants.ACTION_PAUSE
            context.startService(pauseEpisodeIntent)
        }
        super.onReceive(context, intent)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        const val WIDGET_PLAYING_EXTRA = "widget-is-playing"
        const val WIDGET_NO_PLAYING_EXTRA = "widget-no-episode-playing"

        const val PLAY_BUTTON_CLICKED_ACTION = "com.hussain.podcastapp.PLAY_BUTTON_CLICKED_ACTION"
        const val PAUSE_BUTTON_CLICKED_ACTION = "com.hussain.podcastapp.PAUSE_BUTTON_CLICKED_ACTION"
    }
}

