package com.hussain.podcastapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hussain.podcastapp.R;
import com.hussain.podcastapp.model.Entry;
import com.hussain.podcastapp.model.Item;
import com.hussain.podcastapp.model.LookUpResponse;
import com.hussain.podcastapp.utils.AppConstants;
import com.hussain.podcastapp.utils.GlideApp;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RoundedBottomSheet extends BottomSheetDialogFragment {

    private static final String TAG = PodcastListFragment.class.getName();
    @BindView(R.id.tvTitle)
    TextView mTitle;
    @BindView(R.id.tvSummary)
    TextView mSummary;
    @BindView(R.id.ivThumbnail)
    ImageView mThumbnail;
    @BindView(R.id.btView)
    Button mBtView;
    private Entry mEntry;
    private LookUpResponse.Results mResults;
    private ArrayList<Item> mItems;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {
            mEntry = b.getParcelable(AppConstants.ENTRY_KEY);
            mResults = b.getParcelable(AppConstants.RESULTS_KEY);

        }
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        initUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        initUI();
    }

    private void initUI() {
        mTitle.setText(mEntry.getEntryTitle().getLabel());
        mSummary.setText(mEntry.getSummary().getLabel());
        GlideApp.with(this)
                .load(mResults.getArtWork())
                .placeholder(R.drawable.ic_launcher_foreground)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mThumbnail);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_bottom_sheet, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btView)
    public void onViewClick() {
        Intent intent = new Intent(getContext(),EpisodesActivity.class);
        intent.putExtra(AppConstants.FEED_URL_KEY,mResults.getFeedUrl());
        intent.putExtra(AppConstants.ARTWORK_URL, mResults.getArtWork());
        startActivity(intent);
        dismiss();
    }
}
