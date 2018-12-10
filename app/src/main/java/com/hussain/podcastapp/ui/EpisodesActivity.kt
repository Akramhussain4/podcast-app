package com.hussain.podcastapp.ui

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.hussain.podcastapp.R
import com.hussain.podcastapp.adapter.EpisodeAdapter
import com.hussain.podcastapp.base.BaseActivity
import com.hussain.podcastapp.database.ApiInterface
import com.hussain.podcastapp.model.Channel
import com.hussain.podcastapp.model.Item
import com.hussain.podcastapp.model.RssFeed
import com.hussain.podcastapp.utils.AppConstants
import com.hussain.podcastapp.utils.GlideApp
import kotlinx.android.synthetic.main.activity_episodes.*
import kotlinx.android.synthetic.main.layout_error.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class EpisodesActivity : BaseActivity(), EpisodeAdapter.EpisodeClickListener {

    private var mInterstitialAd: InterstitialAd? = null
    private var mArtworkUrl: String? = null
    private var mItems: List<Item>? = null
    private var mChannel: Channel? = null

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        onCreate(savedInstanceState, R.layout.activity_episodes)

        val b = intent
        val mFeedUrl = b.getStringExtra(AppConstants.FEED_URL_KEY)
        mArtworkUrl = b.getStringExtra(AppConstants.ARTWORK_URL)

        if (mFeedUrl != null) {
            getRssFeed(mFeedUrl)
            rvEpisodes?.layoutManager = LinearLayoutManager(this)
        }

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd?.adUnitId = "ca-app-pub-4616631912723447/5671156240"
        mInterstitialAd?.loadAd(AdRequest.Builder().build())
    }

    private fun getRssFeed(url: String) {
        var rssURL = url
        showAnimation(true)
        val uri = Uri.parse(rssURL)
        val protocol = uri.scheme
        val server = uri.authority
        val path = uri.path
        val limit = uri.getQueryParameter("id")
        rssURL = "$protocol://$server$path/"

        val retrofit = Retrofit.Builder()
                .baseUrl(rssURL)
                .client(OkHttpClient())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)
        val rssFeedCall = apiInterface.getRssFeed(limit!!)

        rssFeedCall.enqueue(object : Callback<RssFeed> {
            override fun onResponse(@NonNull call: Call<RssFeed>, @NonNull response: Response<RssFeed>) {
                val data = response.body()
                if (data != null) {
                    mChannel = data.channel
                    mItems = mChannel!!.items
                    setUI()
                } else {
                    coordinatorLayout.visibility = View.GONE
                    error_layout.visibility = View.VISIBLE
                    tvError.setText(R.string.no_podcast)
                    showAnimation(false)
                }
            }

            override fun onFailure(@NonNull call: Call<RssFeed>, @NonNull t: Throwable) {
                Log.d(TAG, t.message)
                Toast.makeText(applicationContext, R.string.user_error_text, Toast.LENGTH_LONG).show()
                finish()
            }
        })
    }

    private fun setUI() {
        if (mChannel != null && mItems != null) {
            val episodesText = mItems?.size.toString() + " " + resources.getString(R.string.episodes_text)
            tvTitle.text = mChannel?.title
            tvDescription.text = Html.fromHtml(mChannel?.description)
            tvEpisodes.text = episodesText
            GlideApp.with(this)
                    .load(mArtworkUrl)
                    .placeholder(R.color.colorPrimary)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivArtwork)
            val adapter = EpisodeAdapter(mItems!!, this)
            rvEpisodes?.adapter = adapter
            showAnimation(false)
        }
        if (mInterstitialAd!!.isLoaded) {
            mInterstitialAd?.show()
        }
    }

    override fun onEpisodeClick(item: Item, position: Int, imageView: ImageView?) {
        val intent = Intent(this, PlayerActivity::class.java)
        val dataBundle = Bundle()
        dataBundle.putParcelable(AppConstants.ITEM_KEY, item)
        dataBundle.putString(AppConstants.SHARE_KEY, mChannel?.shareLink)
        intent.putExtra(AppConstants.BUNDLE_KEY, dataBundle)
        val transitionBundle = ActivityOptions.makeSceneTransitionAnimation(this, imageView, imageView!!.transitionName).toBundle()
        startActivity(intent, transitionBundle)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    public override fun onSaveInstanceState(@NonNull outState: Bundle) {
        outState.putParcelable(AppConstants.SCROLL_POSITION, rvEpisodes?.layoutManager?.onSaveInstanceState())
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        rvEpisodes?.layoutManager?.onRestoreInstanceState(savedInstanceState.getParcelable<Parcelable>(AppConstants.SCROLL_POSITION))
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onToolBarSetUp(toolbar: Toolbar?, actionBar: ActionBar) {
        val tvHeader = toolbar!!.findViewById<TextView>(R.id.tvClassName)
        tvHeader.setText(R.string.app_name)
    }

    companion object {

        private val TAG = EpisodesActivity::class.java.name
    }
}

