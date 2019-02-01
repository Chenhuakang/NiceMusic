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

package com.lzx.nicemusic.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.bean.Personalized;
import com.lzx.starrysky.model.SongInfo;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private List<Personalized> mPersonalizeds;
    private List<SongInfo> mSongInfos;
    private onItemClickListener mOnItemClickListener;
    private boolean isShowPlayList = false;

    public RecyclerViewAdapter(Context context) {
        mContext = context;
        mPersonalizeds = new ArrayList<>();
        mSongInfos = new ArrayList<>();
    }

    public void setPersonalizeds(List<Personalized> personalizeds) {
        mPersonalizeds.clear();
        mPersonalizeds.addAll(personalizeds);
        notifyDataSetChanged();
    }

    public void setSongInfos(List<SongInfo> songInfos, boolean isLoadMore) {
        if (!isLoadMore) {
            mSongInfos.clear();
        }
        mSongInfos.addAll(songInfos);
        notifyDataSetChanged();
    }

    public void setShowPlayList(boolean isShowPlayList) {
        this.isShowPlayList = isShowPlayList;
        notifyDataSetChanged();
    }

    public boolean isShowPlayList() {
        return isShowPlayList;
    }

    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String coverImgUrl;
        String title;
        String artist;
        if (isShowPlayList) {
            SongInfo songInfo = mSongInfos.get(position);
            coverImgUrl = songInfo.getSongCover();
            title = songInfo.getSongName();
            artist = songInfo.getArtist();
            holder.mDurationView.setVisibility(View.VISIBLE);
            holder.mDurationView.setText(DateUtils.formatElapsedTime(songInfo.getDuration()/1000));
            holder.mView.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mSongInfos, position);
                }
            });
        } else {
            Personalized personalized = mPersonalizeds.get(position);
            coverImgUrl = personalized.picUrl;
            title = personalized.name;
            artist = personalized.copywriter;
            holder.mDurationView.setVisibility(View.GONE);
            holder.mView.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(personalized, position);
                }
            });
        }

        Glide.with(mContext)
                .load(coverImgUrl)
                .into(holder.mCoverView);

        holder.mTitleView.setText(title);
        holder.mArtistView.setText(artist);
    }

    @Override
    public int getItemCount() {
        return isShowPlayList ? mSongInfos.size() : mPersonalizeds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mCoverView;
        public final TextView mTitleView;
        public final TextView mArtistView;
        public final TextView mDurationView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCoverView = view.findViewById(R.id.cover);
            mTitleView = view.findViewById(R.id.title);
            mArtistView = view.findViewById(R.id.artist);
            mDurationView = view.findViewById(R.id.duration);
        }
    }

    public interface onItemClickListener {
        void onItemClick(Personalized personalized, int position);

        void onItemClick(List<SongInfo> songInfos, int position);
    }

}
