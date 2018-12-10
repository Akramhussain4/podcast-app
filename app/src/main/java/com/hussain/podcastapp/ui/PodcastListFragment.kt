package com.hussain.podcastapp.ui

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.SearchView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.hussain.podcastapp.R
import com.hussain.podcastapp.adapter.PodcastAdapter
import com.hussain.podcastapp.base.IBaseView
import com.hussain.podcastapp.customview.CustomBottomSheet
import com.hussain.podcastapp.customview.GridAutofitLayoutManager
import com.hussain.podcastapp.database.ApiInterface
import com.hussain.podcastapp.database.AppDatabase
import com.hussain.podcastapp.model.ApiResponse
import com.hussain.podcastapp.model.Entry
import com.hussain.podcastapp.model.LookUpResponse
import com.hussain.podcastapp.utils.AppConstants
import com.hussain.podcastapp.utils.AppExecutors
import kotlinx.android.synthetic.main.fragment_podcasts_list.*
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PodcastListFragment : Fragment(), PodcastAdapter.PodcastClickListener, IBaseView {

    private var mEntryData: List<Entry>? = null
    private var mContext: PodcastListFragment? = null
    private var mAdapter: PodcastAdapter? = null
    private var mCategory: String? = null
    private var mResults: LookUpResponse.Results? = null
    private var mActivityContext: MainActivity? = null
    private var mDatabase: DatabaseReference? = null
    private var mBottomDialog: BottomSheetDialog? = null
    private var mDb: AppDatabase? = null
    private var isClicked = true
    private var mSubscribed = false
    private var mUserId: String? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val b = arguments
        if (b != null) {
            mCategory = b.getString(AppConstants.CATEGORY_KEY)
        }

        mDb = AppDatabase.getInstance(context)
        mDatabase = FirebaseDatabase.getInstance().reference
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            mUserId = user.uid
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_podcasts_list, container, false)
        setHasOptionsMenu(true)
        recyclerView = view.findViewById(R.id.rvPodcast) as RecyclerView
        recyclerView?.layoutManager = GridAutofitLayoutManager(context, 300)
        mContext = this
        return view
    }

    override fun onStart() {
        super.onStart()
        networkCall()
        swiperefresh.setOnRefreshListener { this.networkCall() }
    }

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        mActivityContext = activity as MainActivity
        super.onActivityCreated(savedInstanceState)
    }

    private fun networkCall() {
        showAnimation(true)
        val cacheSize = 10 * 1024 * 1024 // 10 MB
        val cache = Cache(activity!!.cacheDir, cacheSize.toLong())

        val okHttpClient = OkHttpClient.Builder()
                .cache(cache)
                .build()
        val retrofit = Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val apiInterface = retrofit.create(ApiInterface::class.java)
        val call = apiInterface.getPodcastsByGengre(mCategory!!)
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(@NonNull call: Call<ApiResponse>, @NonNull response: Response<ApiResponse>) {
                val mData = response.body()
                showAnimation(false)
                if (mData != null) {
                    swiperefresh.isRefreshing = false
                    mEntryData = mData.feed?.getEntry()
                    mAdapter = PodcastAdapter(mEntryData!!, mContext)
                    recyclerView?.adapter = mAdapter
                }
            }

            override fun onFailure(@NonNull call: Call<ApiResponse>, @NonNull t: Throwable) {
                Log.d(TAG, t.message)
            }
        })
    }

    override fun onPodcastClick(item: Entry, position: Int) {
        if (isClicked) {
            isClicked = false
            showAnimation(true)
            val id = item.feedId?.attributes?.id
            lookUpCall(id, item)
        }
    }

    private fun lookUpCall(id: String?, item: Entry) {
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
                    checkIfSubscribed(item, mResults)
                }
            }

            override fun onFailure(@NonNull call: Call<LookUpResponse>, @NonNull t: Throwable) {
                Log.d(TAG, t.message)
            }
        })
    }

    private fun checkIfSubscribed(item: Entry, mResults: LookUpResponse.Results?) {
        val feedId = item.feedId?.attributes?.id
        AppExecutors.instance?.diskIO?.execute {
            val entry = mDb?.entryDao()?.getPodcast(feedId!!)
            if (entry != null) {
                mSubscribed = true
                mActivityContext?.runOnUiThread { bottomDialog(item, mResults!!, true) }
            } else {
                mActivityContext?.runOnUiThread { bottomDialog(item, mResults!!, false) }
            }
        }
    }

    private fun bottomDialog(item: Entry, results: LookUpResponse.Results, sub: Boolean) {
        mBottomDialog = CustomBottomSheet.showBottomDialog(activity, item, results.artWork!!, { _ ->
            val intent = Intent(context, EpisodesActivity::class.java)
            intent.putExtra(AppConstants.FEED_URL_KEY, mResults?.feedUrl)
            intent.putExtra(AppConstants.ARTWORK_URL, mResults?.artWork)
            showBottomDialog(false)
            startActivity(intent)
        }, { view ->
            if (mSubscribed) {
                handleDelete(item, view as View)
            } else {
                handleInsert(item, view as View)
            }
        }, sub)
        showBottomDialog(true)
        isClicked = true
        showAnimation(false)
    }

    private fun handleDelete(item: Entry, view: View) {
        val btSub = view.findViewById<Button>(R.id.btSubscribe)
        btSub.setText(R.string.subscribe)
        mSubscribed = false
        AppExecutors.instance?.diskIO?.execute { mDb?.entryDao()?.deletePodcast(item.feedId?.attributes?.id!!) }
        mDatabase?.child(mUserId!!)?.child(item.feedId?.attributes?.id!!)?.removeValue()
    }

    private fun handleInsert(item: Entry, view: View) {
        val btSub = view.findViewById<Button>(R.id.btSubscribe)
        btSub.setText(R.string.unsubscribe)
        mSubscribed = true
        AppExecutors.instance?.diskIO?.execute { mDb?.entryDao()?.insertPodcast(item) }
        val postValues = item.toMap()
        mDatabase?.child(mUserId!!)?.child(item.feedId?.attributes?.id!!)?.setValue(postValues)
    }


    private fun showBottomDialog(show: Boolean) {
        if (mBottomDialog != null) {
            if (show) {
                mBottomDialog?.show()
            } else {
                mBottomDialog?.dismiss()
            }
        }
    }

    override fun onStop() {
        showBottomDialog(false)
        super.onStop()
    }

    override fun showAnimation(show: Boolean) {
        if (mActivityContext != null) {
            mActivityContext?.showAnimation(show)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.options_menu, menu)
        val searchManager = activity
                ?.getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val searchView = menu?.findItem(R.id.search)?.actionView as SearchView

        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(activity?.componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                mAdapter?.filter?.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                mAdapter?.filter?.filter(query)
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId
        return if (id == R.id.search) {
            true
        } else super.onOptionsItemSelected(item)
    }

    companion object {

        private val TAG = PodcastListFragment::class.java.name
    }
}