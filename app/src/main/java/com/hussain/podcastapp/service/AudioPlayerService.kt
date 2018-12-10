package com.hussain.podcastapp.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.text.TextUtils
import androidx.annotation.Nullable
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.hussain.podcastapp.R
import com.hussain.podcastapp.model.Item
import com.hussain.podcastapp.ui.PlayerActivity
import com.hussain.podcastapp.utils.AppConstants
import com.hussain.podcastapp.utils.CommonUtils
import com.hussain.podcastapp.widget.PlayerWidget

class AudioPlayerService : Service(), Player.EventListener {

    private val mBinder = LocalBinder()
    private var mPlayer: SimpleExoPlayer? = null
    private var mItem: Item? = null
    private var mPlayerNotificationManager: PlayerNotificationManager? = null

    val playerInstance: SimpleExoPlayer?
        get() {
            if (mItem != null && mPlayer == null && !TextUtils.isEmpty(mItem!!.url)) {
                startPlayer()
            }
            return mPlayer
        }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    private fun releasePlayer() {
        if (mPlayer != null) {
            mPlayerNotificationManager!!.setPlayer(null)
            mPlayer!!.release()
            mPlayer = null
        }
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val b = intent.getBundleExtra(AppConstants.BUNDLE_KEY)
        if (b != null) {
            releasePlayer()
            mItem = b.getParcelable(AppConstants.ITEM_KEY)
        }
        if (mItem != null && mPlayer == null && !TextUtils.isEmpty(mItem!!.url)) {
            startPlayer()
        }
        val action = intent.action
        if (mPlayer != null) {
            if (!TextUtils.isEmpty(action) && action!!.equals(AppConstants.ACTION_PLAY, ignoreCase = true)) {
                mPlayer!!.playWhenReady = true

            }
            if (!TextUtils.isEmpty(action) && action!!.equals(AppConstants.ACTION_PAUSE, ignoreCase = true)) {
                mPlayer!!.playWhenReady = false
            }
        }
        return Service.START_STICKY
    }

    private fun startPlayer() {
        val context = this
        val uri = Uri.parse(mItem!!.url)
        mPlayer = ExoPlayerFactory.newSimpleInstance(context, DefaultTrackSelector())
        val dataSourceFactory = DefaultDataSourceFactory(context,
                Util.getUserAgent(context, getString(R.string.app_name)))
        val cacheDataSourceFactory = CacheDataSourceFactory(
                CommonUtils.getCache(context),
                dataSourceFactory,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        val mediaSource = ExtractorMediaSource.Factory(cacheDataSourceFactory)
                .createMediaSource(uri)
        mPlayer!!.prepare(mediaSource)
        mPlayer!!.addListener(this)
        mPlayer!!.playWhenReady = true
        mPlayerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(context, AppConstants.PLAYBACK_CHANNEL_ID,
                R.string.playback_channel_name,
                AppConstants.PLAYBACK_NOTIFICATION_ID,
                object : PlayerNotificationManager.MediaDescriptionAdapter {
                    override fun getCurrentContentTitle(player: Player): String {
                        return mItem!!.title
                    }

                    @Nullable
                    override fun createCurrentContentIntent(player: Player): PendingIntent {
                        val intent = Intent(context, PlayerActivity::class.java)
                        val serviceBundle = Bundle()
                        serviceBundle.putParcelable(AppConstants.ITEM_KEY, mItem)
                        intent.putExtra(AppConstants.BUNDLE_KEY, serviceBundle)
                        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                    }

                    @Nullable
                    override fun getCurrentContentText(player: Player): String {
                        return mItem!!.summary
                    }

                    @Nullable
                    override fun getCurrentLargeIcon(player: Player, callback: PlayerNotificationManager.BitmapCallback): Bitmap? {
                        return null
                    }
                }
        )
        mPlayerNotificationManager!!.setNotificationListener(object : PlayerNotificationManager.NotificationListener {
            override fun onNotificationStarted(notificationId: Int, notification: Notification) {
                startForeground(notificationId, notification)
            }

            override fun onNotificationCancelled(notificationId: Int) {
                val intent = Intent(context, PlayerWidget::class.java)
                intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                val ids = AppWidgetManager.getInstance(application)
                        .getAppWidgetIds(ComponentName(application, PlayerWidget::class.java))
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                intent.putExtra(PlayerWidget.WIDGET_NO_PLAYING_EXTRA, "")
                sendBroadcast(intent)
                stopSelf()
                stopForeground(true)
            }
        })
        mPlayerNotificationManager!!.setPlayer(mPlayer)
    }

    private fun updateWidget(playWhenReady: Boolean) {
        val intent = Intent(this, PlayerWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(application)
                .getAppWidgetIds(ComponentName(application, PlayerWidget::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        val serviceBundle = Bundle()
        serviceBundle.putParcelable(AppConstants.ITEM_KEY, mItem)
        intent.putExtra(AppConstants.BUNDLE_KEY, serviceBundle)
        intent.putExtra(PlayerWidget.WIDGET_PLAYING_EXTRA, playWhenReady)
        sendBroadcast(intent)
    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {

    }

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {}

    override fun onLoadingChanged(isLoading: Boolean) {}

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        updateWidget(playWhenReady)
    }

    override fun onRepeatModeChanged(repeatMode: Int) {

    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

    }

    override fun onPlayerError(error: ExoPlaybackException?) {

    }

    override fun onPositionDiscontinuity(reason: Int) {

    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

    }

    override fun onSeekProcessed() {

    }

    inner class LocalBinder : Binder() {
        val service: AudioPlayerService
            get() = this@AudioPlayerService
    }
}