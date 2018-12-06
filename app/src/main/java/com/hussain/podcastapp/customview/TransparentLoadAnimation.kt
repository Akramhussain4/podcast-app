package com.hussain.podcastapp.customview

import android.app.Dialog
import android.content.Context
import android.util.Log

import com.hussain.podcastapp.R

class TransparentLoadAnimation(context: Context) {

    private val dialog: Dialog?

    init {
        dialog = Dialog(context, R.style.TransparentLoadingAnimation)
        dialog.setContentView(R.layout.loader_animation)
        dialog.setCancelable(false)
    }


    fun showProgress() {
        if (dialog == null) {
            Log.d(TAG, "show progress is null")
            return
        }
        dialog.show()
    }

    fun hideProgress() {
        dialog!!.dismiss()
    }

    companion object {

        private val TAG = TransparentLoadAnimation::class.java.name
    }
}
