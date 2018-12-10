package com.hussain.podcastapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.hussain.podcastapp.R
import com.hussain.podcastapp.base.BaseActivity

class AboutActivity : BaseActivity() {

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        onCreate(savedInstanceState, R.layout.activity_about)
    }

    override fun onToolBarSetUp(toolbar: Toolbar?, actionBar: ActionBar) {
        val tvHeader = toolbar!!.findViewById<TextView>(R.id.tvClassName)
        tvHeader.setText(R.string.about)
    }

    fun emailClick(view: View) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "akramhussain0007@gmail.com"))
        startActivity(intent)
    }

    fun linkedInClick(view: View) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/akramhussain4/"))
        startActivity(intent)
    }

    fun githubClick(view: View) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Akramhussain4"))
        startActivity(intent)
    }

    fun googlePlayClick(view: View) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=6822584155686265410"))
        startActivity(intent)
    }

    fun coffeeClick(view: View) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.buymeacoffee.com/akramhussain4"))
        startActivity(intent)
    }
}
