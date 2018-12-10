package com.hussain.podcastapp.customview

import android.app.Activity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hussain.podcastapp.R
import com.hussain.podcastapp.model.Entry

object CustomBottomSheet {

    fun showBottomDialog(activity: Activity?, item: Entry, artWork: String, viewClickListener: (Any) -> Unit, subscribeClicklistener: (Any) -> Unit, subscribed: Boolean): BottomSheetDialog? {

        if (activity == null) {
            return null
        }

        val bottomSheetBehavior = activity.layoutInflater.inflate(R.layout.layout_bottom_sheet, null)
        val bottomSheetDialog = BottomSheetDialog(activity)
        bottomSheetDialog.setContentView(bottomSheetBehavior)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.window!!.findViewById<View>(R.id.design_bottom_sheet).setBackgroundResource(R.drawable.bottom_sheet_background)
        val tvTitle = bottomSheetDialog.findViewById<TextView>(R.id.tvTitle)
        val tvSummary = bottomSheetDialog.findViewById<TextView>(R.id.tvSummary)
        val ivThumbnail = bottomSheetDialog.findViewById<ImageView>(R.id.ivThumbnail)
        val btView = bottomSheetDialog.findViewById<Button>(R.id.btView)
        val btSubscribe = bottomSheetDialog.findViewById<Button>(R.id.btSubscribe)
        if (subscribed) {
            btSubscribe?.setText(R.string.unsubscribe)
        } else {
            btSubscribe?.setText(R.string.subscribe)
        }
        tvTitle?.text = item.entryTitle!!.labelTitle
        tvSummary?.text = item.summary!!.label
        Glide.with(activity)
                .load(artWork)
                .into(ivThumbnail!!)
        btView?.setOnClickListener(viewClickListener)
        btSubscribe?.setOnClickListener(subscribeClicklistener)
        return bottomSheetDialog
    }
}

