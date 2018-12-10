package com.hussain.podcastapp.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hussain.podcastapp.R
import com.hussain.podcastapp.model.Item
import com.hussain.podcastapp.utils.GlideApp
import kotlinx.android.synthetic.main.layout_episode_item.view.*

class EpisodeAdapter(private val mItemList: List<Item>, private val mClickListener: EpisodeClickListener?) : RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>() {
    private var mContext: Context? = null

    init {
        notifyDataSetChanged()
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull viewGroup: ViewGroup, i: Int): EpisodeViewHolder {
        mContext = viewGroup.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.layout_episode_item, viewGroup, false)
        return EpisodeViewHolder(view)
    }

    override fun onBindViewHolder(@NonNull episodeViewHolder: EpisodeViewHolder, i: Int) {
        val item = mItemList[i]
        episodeViewHolder.mTitle.text = item.title
        episodeViewHolder.mSummary.text = Html.fromHtml(item.summary)
        val drawable = LottieDrawable()
        drawable.setImageAssetDelegate { null }
        GlideApp.with(mContext!!)
                .load(item.image)
                .centerCrop()
                .placeholder(R.drawable.placeholder_square)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(episodeViewHolder.mThumb)
        episodeViewHolder.itemView.setOnClickListener { view ->
            mClickListener?.onEpisodeClick(item, i, episodeViewHolder.mThumb)
        }
    }

    override fun getItemCount(): Int {
        return mItemList.size
    }

    interface EpisodeClickListener {
        fun onEpisodeClick(item: Item, position: Int, imageView: ImageView?)
    }

    class EpisodeViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mThumb = itemView.ivThumb!!
        var mTitle = itemView.tvTitle!!
        var mSummary = itemView.tvSummary!!
    }
}
