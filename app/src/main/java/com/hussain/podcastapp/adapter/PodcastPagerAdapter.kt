package com.hussain.podcastapp.adapter

import android.os.Bundle
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hussain.podcastapp.ui.PodcastListFragment
import com.hussain.podcastapp.utils.AppConstants
import java.util.*

class PodcastPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private var list: List<String>? = null

    init {
        list = ArrayList()
    }

    override fun getItem(i: Int): Fragment? {
        when (i) {
            0 -> {
                val techFragment = PodcastListFragment()
                val techB = Bundle()
                techB.putString(AppConstants.CATEGORY_KEY, AppConstants.TECH_GENRE)
                techFragment.arguments = techB
                return techFragment
            }
            1 -> {
                val scienceFragment = PodcastListFragment()
                val sciB = Bundle()
                sciB.putString(AppConstants.CATEGORY_KEY, AppConstants.SCIENCE_GENRE)
                scienceFragment.arguments = sciB
                return scienceFragment
            }
            2 -> {
                val healthFragment = PodcastListFragment()
                val healB = Bundle()
                healB.putString(AppConstants.CATEGORY_KEY, AppConstants.HEALTH_GENRE)
                healthFragment.arguments = healB
                return healthFragment
            }
            3 -> {
                val businessFragment = PodcastListFragment()
                val bussB = Bundle()
                bussB.putString(AppConstants.CATEGORY_KEY, AppConstants.BUSINESS_GENRE)
                businessFragment.arguments = bussB
                return businessFragment
            }
            4 -> {
                val sportsFragment = PodcastListFragment()
                val sportB = Bundle()
                sportB.putString(AppConstants.CATEGORY_KEY, AppConstants.SPORTS_GENRE)
                sportsFragment.arguments = sportB
                return sportsFragment
            }
        }
        return null
    }

    fun refresh(list: List<String>) {
        this.list = list
        notifyDataSetChanged()
    }

    @Nullable
    override fun getPageTitle(position: Int): CharSequence {
        return list!![position]
    }

    override fun getCount(): Int {
        return list!!.size
    }
}
