package com.hussain.podcastapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.hussain.podcastapp.R
import com.hussain.podcastapp.adapter.PodcastPagerAdapter
import com.hussain.podcastapp.base.BaseActivity
import com.hussain.podcastapp.utils.GlideApp
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import java.util.*

class MainActivity : BaseActivity() {

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        onCreate(savedInstanceState, R.layout.activity_main)
        mFirebaseUser = FirebaseAuth.getInstance().currentUser
        setUI()
    }

    private fun setUI() {
        val adapter = PodcastPagerAdapter(supportFragmentManager)
        vp.adapter = adapter

        val types = ArrayList<String>()
        types.add("TECH")
        types.add("Science")
        types.add("Health")
        types.add("Business")
        types.add("Sports")
        adapter.refresh(types)
        tabLayout.setupWithViewPager(vp, true)

        DrawerImageLoader.init(object : AbstractDrawerImageLoader() {
            override fun set(imageView: ImageView?, uri: Uri?, placeholder: Drawable?) {
                GlideApp.with(imageView!!.context).load(mFirebaseUser?.photoUrl).into(imageView)
            }
        })
        DrawerBuilder().withActivity(this).build()

        val headerResult = AccountHeaderBuilder()
                .withActivity(this)
                .withSelectionListEnabledForSingleProfile(false)
                .withHeaderBackground(R.drawable.header_background)
                .addProfiles(ProfileDrawerItem()
                        .withName(mFirebaseUser?.displayName)
                        .withEmail(mFirebaseUser?.email)
                        .withIcon(mFirebaseUser?.photoUrl))
                .withOnAccountHeaderListener { _, _, _ -> false }
                .build()

        DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        PrimaryDrawerItem().withIdentifier(1)
                                .withName("Home").withIcon(R.drawable.ic_home)
                                .withSelectable(true),
                        PrimaryDrawerItem().withIdentifier(2)
                                .withName("Subscriptions").withIcon(R.drawable.ic_heart_box)
                                .withSelectable(false),
                        DividerDrawerItem(),
                        SecondaryDrawerItem().withIdentifier(3).withIcon(R.drawable.ic_info)
                                .withName("About").withSelectable(false))
                .withOnDrawerItemClickListener { _, _, drawerItem ->
                    if (drawerItem != null) {
                        when (drawerItem.identifier.toInt()) {
                            2 -> startActivity(Intent(this, SubscribeActivity::class.java))
                            3 -> startActivity(Intent(this, AboutActivity::class.java))
                        }
                    }
                    false
                }.build()
    }


    override fun onToolBarSetUp(toolbar: Toolbar?, actionBar: ActionBar) {
        toolbar?.title = "Pod play"
        toolbar?.setTitleTextColor(getColor(R.color.colorWhite))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return false
    }
}
