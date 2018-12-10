package com.hussain.podcastapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.florent37.glidepalette.GlidePalette
import com.hussain.podcastapp.R
import com.hussain.podcastapp.model.Entry
import com.hussain.podcastapp.utils.GlideApp
import kotlinx.android.synthetic.main.layout_podcast_item.view.*
import java.util.*

class PodcastAdapter(private val mEntry: List<Entry>, private val mClickListener: PodcastClickListener?) : RecyclerView.Adapter<PodcastAdapter.PodcastViewHolder>(), Filterable {
    private var mFilteredList: List<Entry>? = null
    private var mContext: Context? = null

    init {
        this.mFilteredList = mEntry
        notifyDataSetChanged()
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, i: Int): PodcastViewHolder {
        mContext = viewGroup.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.layout_podcast_item, viewGroup, false)
        return PodcastViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull podcastViewHolder: PodcastViewHolder, i: Int) {
        val item = mFilteredList!![i]
        GlideApp.with(mContext!!)
                .load(mFilteredList!![i].image!![2].label)
                .placeholder(R.drawable.placeholder_square)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(GlidePalette.with(mFilteredList!![i].image!![2].label)
                        .intoCallBack { palette ->
                            val swatch = palette?.vibrantSwatch
                            if (swatch != null) {
                                podcastViewHolder.mLinearLayout.setBackgroundColor(swatch.rgb)
                                podcastViewHolder.mTitle.setTextColor(swatch.bodyTextColor)
                            }
                        }).into(podcastViewHolder.mThumbnail)
        podcastViewHolder.mTitle.text = item.entryTitle?.labelTitle
        podcastViewHolder.itemView.setOnClickListener { v ->
            mClickListener?.onPodcastClick(item, podcastViewHolder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return mFilteredList!!.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                mFilteredList = if (charString.isEmpty()) {
                    mEntry
                } else {
                    val filteredList = ArrayList<Entry>()
                    for (row in mEntry) {
                        if (row.entryTitle!!.labelTitle!!.toLowerCase().contains(charString.toLowerCase()) || row.entryTitle!!.labelTitle!!.contains(charSequence)) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }

                val filterResults = Filter.FilterResults()
                filterResults.values = mFilteredList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                mFilteredList = filterResults.values as ArrayList<Entry>
                notifyDataSetChanged()
            }
        }
    }

    interface PodcastClickListener {
        fun onPodcastClick(item: Entry, position: Int)
    }

    class PodcastViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mThumbnail = itemView.podcast_icon!!
        val mTitle = itemView.podcast_title!!
        var mLinearLayout = itemView.podcast_holder!!
    }
}
