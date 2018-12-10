package com.hussain.podcastapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.hussain.podcastapp.R
import com.hussain.podcastapp.adapter.PodcastAdapter
import com.hussain.podcastapp.base.BaseActivity
import com.hussain.podcastapp.customview.GridAutofitLayoutManager
import com.hussain.podcastapp.database.ApiInterface
import com.hussain.podcastapp.database.AppDatabase
import com.hussain.podcastapp.model.Entry
import com.hussain.podcastapp.model.LookUpResponse
import com.hussain.podcastapp.utils.AppConstants
import com.hussain.podcastapp.utils.AppExecutors
import kotlinx.android.synthetic.main.activity_subscribe.*
import kotlinx.android.synthetic.main.layout_error.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class SubscribeActivity : BaseActivity(), PodcastAdapter.PodcastClickListener {

    private var mEntryData: List<Entry>? = null
    private var mFirebaseData: MutableList<Entry>? = null
    private var mResults: LookUpResponse.Results? = null
    private var mDb: AppDatabase? = null
    private var mDatabase: DatabaseReference? = null

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        onCreate(savedInstanceState, R.layout.activity_subscribe)

        mDb = AppDatabase.getInstance(this)
        val mUserId = FirebaseAuth.getInstance().currentUser?.uid
        mDatabase = FirebaseDatabase.getInstance().reference.child(mUserId!!)
        mFirebaseData = ArrayList()
        recyclerView.layoutManager = GridAutofitLayoutManager(this, 300)
    }

    override fun onStart() {
        super.onStart()
        networkCall()
        showAnimation(true)
    }

    private fun networkCall() {
        mDatabase!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    mFirebaseData!!.add(postSnapshot.getValue(Entry::class.java)!!)
                }
                insertData()
            }

            override fun onCancelled(@NonNull error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    private fun insertData() {
        showAnimation(false)
        if (mFirebaseData != null && mFirebaseData!!.size > 0) {
            AppExecutors.instance!!.diskIO.execute { mDb!!.entryDao().insertPodcastList(mFirebaseData!!) }
            setUI()
        } else {
            recyclerView.visibility = View.GONE
            error_layout.visibility = View.VISIBLE
            tvError.setText(R.string.no_subscriptions)
        }
    }

    private fun setUI() {
        AppExecutors.instance?.diskIO?.execute {
            mEntryData = mDb?.entryDao()?.loadAllPodcasts()
            if (mEntryData != null && mEntryData!!.isNotEmpty()) {
                runOnUiThread {
                    val podcastAdapter = PodcastAdapter(mEntryData!!, this@SubscribeActivity)
                    recyclerView.adapter = podcastAdapter
                }
            } else {
                runOnUiThread {
                    recyclerView.visibility = View.GONE
                    error_layout.visibility = View.VISIBLE
                    tvError.setText(R.string.no_subscriptions)
                }
            }
        }
    }

    override fun onToolBarSetUp(toolbar: Toolbar?, actionBar: ActionBar) {
        val tvHeader = toolbar?.findViewById<TextView>(R.id.tvClassName)
        tvHeader?.setText(R.string.subscriptions)
    }

    override fun onPodcastClick(item: Entry, position: Int) {
        showAnimation(true)
        val id = item.feedId?.attributes?.id
        lookUpCall(id)
    }

    private fun lookUpCall(id: String?) {
        val retrofit = Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)
        val call1 = apiInterface.getPlaylist(id!!)
        call1.enqueue(object : Callback<LookUpResponse> {
            override fun onResponse(@NonNull call: Call<LookUpResponse>, @NonNull response: Response<LookUpResponse>) {
                val data = response.body()
                if (data != null) {
                    mResults = data.results!![0]
                    openEpisode(mResults!!)
                }
            }

            override fun onFailure(@NonNull call: Call<LookUpResponse>, @NonNull t: Throwable) {
                Log.d(TAG, t.message)
            }
        })
    }

    private fun openEpisode(results: LookUpResponse.Results) {
        showAnimation(false)
        val intent = Intent(this, EpisodesActivity::class.java)
        intent.putExtra(AppConstants.FEED_URL_KEY, results.feedUrl)
        intent.putExtra(AppConstants.ARTWORK_URL, results.artWork)
        startActivity(intent)
    }

    companion object {
        private val TAG = SubscribeActivity::class.java.name
    }
}
