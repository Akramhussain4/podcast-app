package com.hussain.podcastapp.base

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import com.google.firebase.auth.FirebaseUser
import com.hussain.podcastapp.R
import com.hussain.podcastapp.customview.TransparentLoadAnimation

abstract class BaseActivity : AppCompatActivity(), IBaseView {


    private var loadAnimation: TransparentLoadAnimation? = null
    @BindView(R.id.toolbar)
    var mToolbar: Toolbar? = null

    var mFirebaseUser: FirebaseUser? = null

    protected fun onCreate(savedInstanceState: Bundle, resourceLayout: Int) {
        super.onCreate(savedInstanceState)
        setContentView(resourceLayout)
        ButterKnife.bind(this)
        initProgress()
        setUpToolBar()
    }

    private fun initProgress() {
        loadAnimation = TransparentLoadAnimation(this)
    }

    private fun setUpToolBar() {
        setSupportActionBar(mToolbar)
        if (supportActionBar != null) {
            val actionBar = supportActionBar
            actionBar!!.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
        }
        supportActionBar?.let { onToolBarSetUp(mToolbar, it) }
    }

    abstract fun onToolBarSetUp(toolbar: Toolbar?, actionBar: ActionBar)

    override fun showAnimation(show: Boolean) {
        synchronized("SHOW_LOADER_ANIMATION") {
            runOnUiThread {
                if (loadAnimation == null) {
                    Log.d(TAG, "This progress animation is null")
                    return@runOnUiThread
                }
                if (show) {
                    loadAnimation!!.showProgress()
                } else {
                    loadAnimation!!.hideProgress()
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        showAnimation(false)
    }

    companion object {
        private val TAG = BaseActivity::class.java.name
    }
}
