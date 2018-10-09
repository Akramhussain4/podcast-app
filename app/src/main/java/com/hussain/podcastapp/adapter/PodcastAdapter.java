package com.hussain.podcastapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.florent37.glidepalette.GlidePalette;
import com.hussain.podcastapp.R;
import com.hussain.podcastapp.model.Entry;
import com.hussain.podcastapp.utils.GlideApp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PodcastAdapter extends RecyclerView.Adapter<PodcastAdapter.PodcastViewHolder> implements Filterable {

    private List<Entry> mEntry;
    private List<Entry> mFilteredList;
    private PodcastClickListener mClickListener;
    private Context mContext;

    public PodcastAdapter(List<Entry> entry, PodcastClickListener clickListener){
        this.mEntry = entry;
        this.mFilteredList = entry;
        this.mClickListener = clickListener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PodcastViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_podcast_item, viewGroup, false);
        return new PodcastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PodcastViewHolder podcastViewHolder, final int i) {
        final Entry item = mFilteredList.get(i);
        GlideApp.with(mContext)
                .load(mFilteredList.get(i).getImage().get(2).getLabel())
                .placeholder(R.color.colorPrimary)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(GlidePalette.with(mFilteredList.get(i).getImage().get(2).getLabel())
                        .intoCallBack(palette -> {
                            Palette.Swatch swatch = palette.getVibrantSwatch();
                            if (swatch != null) {
                                podcastViewHolder.mLinearLayout.setBackgroundColor(swatch.getRgb());
                                podcastViewHolder.mTitle.setTextColor(swatch.getBodyTextColor());
                            }
                        })).into(podcastViewHolder.mThumbnail);
        podcastViewHolder.mTitle.setText(item.getEntryTitle().getLabelTitle());
        podcastViewHolder.itemView.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onPodcastClick(item, podcastViewHolder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredList = mEntry;
                } else {
                    List<Entry> filteredList = new ArrayList<>();
                    for (Entry row : mEntry) {
                        if (row.getEntryTitle().labelTitle.toLowerCase().contains(charString.toLowerCase()) || row.getEntryTitle().labelTitle.contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }
                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<Entry>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface PodcastClickListener{
        void onPodcastClick(Entry item, int position);
    }

    class PodcastViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.podcast_icon)
        ImageView mThumbnail;
        @BindView(R.id.podcast_title)
        TextView mTitle;
        @BindView(R.id.podcast_holder)
        LinearLayout mLinearLayout;

        PodcastViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

    }
}
