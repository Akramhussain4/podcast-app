package com.hussain.podcastapp.customview;

import android.app.Activity;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hussain.podcastapp.R;
import com.hussain.podcastapp.model.Entry;
import com.hussain.podcastapp.utils.GlideApp;

import java.util.Objects;

public class CustomBottomSheet {

    public static BottomSheetDialog showBottomDialog(Activity activity, Entry item, String artWork, View.OnClickListener viewClickListener, View.OnClickListener subscribeClicklistener, boolean subscribed) {

        if (activity == null) {
            return null;
        }

        View bottomSheetBehavior = activity.getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
        BottomSheetDialog bottomSheetDialog = new android.support.design.widget.BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(bottomSheetBehavior);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        Objects.requireNonNull(bottomSheetDialog.getWindow()).findViewById(R.id.design_bottom_sheet).setBackgroundResource(R.drawable.bottom_sheet_background);
        TextView tvTitle = bottomSheetDialog.findViewById(R.id.tvTitle);
        TextView tvSummary = bottomSheetDialog.findViewById(R.id.tvSummary);
        ImageView ivThumbnail = bottomSheetDialog.findViewById(R.id.ivThumbnail);
        Button btView = bottomSheetDialog.findViewById(R.id.btView);
        Button btSubscribe = bottomSheetDialog.findViewById(R.id.btSubscribe);
        if (subscribed) {
            btSubscribe.setText("UNSUBSCRIBE");
        } else {
            btSubscribe.setText("SUBSCRIBE");
        }
        tvTitle.setText(item.getEntryTitle().getLabelTitle());
        tvSummary.setText(item.getSummary().getLabel());
        GlideApp.with(activity)
                .load(artWork)
                .placeholder(R.color.colorPrimary)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivThumbnail);
        if (btView != null) {
            btView.setOnClickListener(viewClickListener);
        }
        if (btSubscribe != null) {
            btSubscribe.setOnClickListener(subscribeClicklistener);
        }
        return bottomSheetDialog;
    }
}

