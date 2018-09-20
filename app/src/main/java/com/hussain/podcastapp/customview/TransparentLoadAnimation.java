package com.hussain.podcastapp.customview;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import com.hussain.podcastapp.R;

public class TransparentLoadAnimation {

    private static final String TAG = TransparentLoadAnimation.class.getName();

    private Dialog dialog;

    public TransparentLoadAnimation(Context context) {
        dialog = new Dialog(context, R.style.TransparentLoadingAnimation);
        dialog.setContentView(R.layout.loader_animation);
        dialog.setCancelable(false);
    }


    public void showProgress() {
        if (dialog == null) {
            Log.d(TAG, "show progress is null");
            return;
        }
        dialog.show();
    }

    public void hideProgress() {
        dialog.dismiss();
    }
}
