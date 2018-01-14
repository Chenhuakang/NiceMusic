package com.lzx.nicemusic.module.main.sectioned;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzx.nicemusic.R;
import com.lzx.nicemusic.bean.MusicInfo;
import com.lzx.nicemusic.utils.GlideUtil;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by xian on 2018/1/13.
 */

public class NewMusicSection extends StatelessSection {
    private Context mContext;
    private List<MusicInfo> mMusicInfos;

    public NewMusicSection(Context context, List<MusicInfo> newMusics) {
        super(new SectionParameters.Builder(R.layout.item_item_music)
                .headerResourceId(R.layout.item_header_title).build());
        mContext = context;
        mMusicInfos = newMusics;
    }

    @Override
    public int getContentItemsTotal() {
        return mMusicInfos.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ItemViewHolder holder = (ItemViewHolder) viewHolder;
        MusicInfo musicInfo = mMusicInfos.get(position);
        holder.mMusicTitle.setText(musicInfo.getSongname());
        GlideUtil.loadImageByUrl(mContext, musicInfo.getAlbumpicBig(), holder.mMusicCover);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder) {
        HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
        holder.tvTitle.setText("流行热歌");
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        HeaderViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.header_title);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView mMusicCover;
        TextView mMusicTitle;

        ItemViewHolder(View view) {
            super(view);
            mMusicCover = view.findViewById(R.id.music_cover);
            mMusicTitle = view.findViewById(R.id.music_title);
        }
    }
}
