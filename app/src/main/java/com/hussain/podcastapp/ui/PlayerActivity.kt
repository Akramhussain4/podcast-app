package com.hussain.podcastapp.ui

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.text.Html
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Util
import com.google.android.gms.ads.AdRequest
import com.hussain.podcastapp.R
import com.hussain.podcastapp.base.BaseActivity
import com.hussain.podcastapp.model.Item
import com.hussain.podcastapp.service.AudioPlayerService
import com.hussain.podcastapp.utils.AppConstants
import com.hussain.podcastapp.utils.GlideApp
import kotlinx.android.synthetic.main.activity_player.*

class PlayerActivity : BaseActivity() {

    private var mTitle: String? = null
    private var mSummary: String? = null
    private var mImage: String? = null
    private var mService: AudioPlayerService? = null
    private var mPlayer: SimpleExoPlayer? = null
    private var shareableLink: String? = null
    private var mBound = false
    private var playerPosition: Long = 0
    private var mSavedState: Bundle? = null
    private var getPlayerWhenReady: Boolean = false

    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            val binder = iBinder as AudioPlayerService.LocalBinder
            mService = binder.service
            mBound = true
            initializePlayer()
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mBound = false
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        onCreate(savedInstanceState, R.layout.activity_player)
        mSavedState = savedInstanceState
        val b = intent.getBundleExtra(AppConstants.BUNDLE_KEY)
        if (b != null) {
            val item = b.getParcelable<Item>(AppConstants.ITEM_KEY)
            shareableLink = b.getString(AppConstants.SHARE_KEY)
            startPlayerService(item)
        }
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun startPlayerService(item: Item?) {
        if (item != null) {
            mImage = item.image
            mTitle = item.title
            mSummary = item.summary
            intent = Intent(this, AudioPlayerService::class.java)
            val serviceBundle = Bundle()
            serviceBundle.putParcelable(AppConstants.ITEM_KEY, item)
            intent!!.putExtra(AppConstants.BUNDLE_KEY, serviceBundle)
            stopService(intent)
            Util.startForegroundService(this, intent)
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun restoreSeekPos(savedInstanceState: Bundle?) {
        if (savedInstanceState != null && mPlayer != null) {
            playerPosition = savedInstanceState.getLong("PLAYER_POSITION")
            getPlayerWhenReady = savedInstanceState.getBoolean("PLAY_WHEN_READY")
            mPlayer?.seekTo(playerPosition)
            mPlayer?.playWhenReady = getPlayerWhenReady
        }
    }

    private fun initializePlayer() {
        if (mBound) {
            mPlayer = mService?.playerInstance
            video_view.player = mPlayer
            restoreSeekPos(mSavedState)
        }
    }

    public override fun onStart() {
        super.onStart()
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE)
        initializePlayer()
        setUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (mPlayer != null) {
            playerPosition = mPlayer!!.currentPosition
            outState.putLong("PLAYER_POSITION", playerPosition)
            getPlayerWhenReady = mPlayer!!.playWhenReady
            outState.putBoolean("PLAY_WHEN_READY", getPlayerWhenReady)
        }
        super.onSaveInstanceState(outState)
    }

    private fun setUI() {
        tvTitle.text = mTitle
        tvSummary.text = Html.fromHtml(mSummary)
        GlideApp.with(this)
                .load(mImage)
                .placeholder(R.drawable.podcast_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivThumbnail)
        video_view.useController = true
        video_view.showController()
        video_view.controllerAutoShow = true
        video_view.controllerHideOnTouch = false
    }

    override fun onStop() {
        unbindService(mConnection)
        mBound = false
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.player_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share_podcast -> {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, mTitle)
                shareIntent.putExtra(Intent.EXTRA_TEXT, mTitle + "\n\n" + shareableLink)
                shareIntent.type = "text/plain"
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_text)))
                return true
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onToolBarSetUp(toolbar: Toolbar?, actionBar: ActionBar) {
        val tvHeader = toolbar!!.findViewById<TextView>(R.id.tvClassName)
        tvHeader.setText(R.string.app_name)
    }
}
