/*
 * Copyright (c) 2016. André Mion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lzx.nicemusic.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.lzx.nicemusic.R
import com.lzx.nicemusic.bean.Personalized
import com.lzx.starrysky.model.SongInfo

import java.util.ArrayList

class RecyclerViewAdapter(private val mContext: Context) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    private val mPersonalizeds: MutableList<Personalized>
    private val mSongInfos: MutableList<SongInfo>
    private var mOnItemClickListener: OnItemClickListener? = null
    var isShowPlayList = false
        set(isShowPlayList) {
            field = isShowPlayList
            notifyDataSetChanged()
        }

    init {
        mPersonalizeds = ArrayList()
        mSongInfos = ArrayList()
    }

    fun setPersonalised(personalizeds: List<Personalized>) {
        mPersonalizeds.clear()
        mPersonalizeds.addAll(personalizeds)
        notifyDataSetChanged()
    }

    fun setSongInfos(songInfos: List<SongInfo>, isLoadMore: Boolean) {
        if (!isLoadMore) {
            mSongInfos.clear()
        }
        mSongInfos.addAll(songInfos)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mOnItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.content_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val coverImgUrl: String?
        val title: String?
        val artist: String?
        if (this.isShowPlayList) {
            val songInfo = mSongInfos[position]
            coverImgUrl = songInfo.songCover
            title = songInfo.songName
            artist = songInfo.artist
            holder.mDurationView.visibility = View.VISIBLE
            holder.mDurationView.text = DateUtils.formatElapsedTime(songInfo.duration / 1000)
            holder.mView.setOnClickListener { v ->
                if (mOnItemClickListener != null) {
                    mOnItemClickListener!!.onItemClick(mSongInfos, position)
                }
            }
        } else {
            val personalized = mPersonalizeds[position]
            coverImgUrl = personalized.picUrl
            title = personalized.name
            artist = personalized.copywriter
            holder.mDurationView.visibility = View.GONE
            holder.mView.setOnClickListener { v ->
                if (mOnItemClickListener != null) {
                    mOnItemClickListener!!.onItemClick(personalized, position)
                }
            }
        }

        Glide.with(mContext)
                .load(coverImgUrl)
                .into(holder.mCoverView)

        holder.mTitleView.text = title
        holder.mArtistView.text = artist
    }

    override fun getItemCount(): Int {
        return if (this.isShowPlayList) mSongInfos.size else mPersonalizeds.size
    }

    class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mCoverView: ImageView = mView.findViewById(R.id.cover)
        val mTitleView: TextView = mView.findViewById(R.id.title)
        val mArtistView: TextView = mView.findViewById(R.id.artist)
        val mDurationView: TextView = mView.findViewById(R.id.duration)
    }

    interface OnItemClickListener {
        fun onItemClick(personalized: Personalized, position: Int)

        fun onItemClick(songInfos: List<SongInfo>, position: Int)
    }

}
