package com.hussain.podcastapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hussain.podcastapp.R;
import com.hussain.podcastapp.model.Item;
import com.hussain.podcastapp.utils.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder> {

    private List<Item> mItemList;
    private EpisodeClickListener mClickListener;
    private Context mContext;

    public EpisodeAdapter(List<Item> items, EpisodeClickListener clickListener) {
        this.mItemList = items;
        this.mClickListener = clickListener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_episode_item, viewGroup, false);
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder episodeViewHolder, int i) {
        int position = i;
        final Item item = mItemList.get(position);
        episodeViewHolder.mTitle.setText(item.getTitle());
        episodeViewHolder.mSummary.setText(item.getSummary());
        episodeViewHolder.mDuration.setText(item.getDuration());
        GlideApp.with(mContext)
                .load(item.getImage())
                .centerCrop()
                .placeholder(R.color.colorPrimary)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(episodeViewHolder.mThumb);
        episodeViewHolder.itemView.setOnClickListener(view -> {
            if (mClickListener != null) {
                mClickListener.onEpisodeClick(item, position, episodeViewHolder.mThumb);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public interface EpisodeClickListener {
        void onEpisodeClick(Item item, int position, ImageView imageView);
    }

    class EpisodeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivThumb)
        ImageView mThumb;
        @BindView(R.id.tvTitle)
        TextView mTitle;
        @BindView(R.id.tvSummary)
        TextView mSummary;
        @BindView(R.id.tvDuration)
        TextView mDuration;

        EpisodeViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
