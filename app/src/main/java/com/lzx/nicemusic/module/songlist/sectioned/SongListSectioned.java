package com.lzx.nicemusic.module.songlist.sectioned;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzx.musiclibrary.aidl.model.MusicInfo;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.utils.FormatUtil;
import com.lzx.nicemusic.utils.GlideUtil;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * @author lzx
 * @date 2018/2/5
 */

public class SongListSectioned extends StatelessSection {

    private Context mContext;
    private List<MusicInfo> mMusicInfos;

    public SongListSectioned(Context context, List<MusicInfo> list) {
        super(new SectionParameters.Builder(R.layout.section_song_list).build());
        mContext = context;
        mMusicInfos = list;
    }

    @Override
    public int getContentItemsTotal() {
        return mMusicInfos.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ItemHolder holder = (ItemHolder) viewHolder;
        MusicInfo musicInfo = mMusicInfos.get(position);
        holder.mSongNum.setText(String.valueOf((position + 1)));
        holder.mMusicName.setText(musicInfo.musicTitle);
        holder.mMusicTime.setText(FormatUtil.formatMusicTime(musicInfo.musicDuration));
        holder.mMusicTitle.setText(musicInfo.albumArtist + "·" + musicInfo.albumTitle);
        GlideUtil.loadImageByUrl(mContext, musicInfo.musicCover, holder.mMusicCover);
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        ImageView mMusicCover;
        TextView mSongNum, mMusicName, mMusicTitle, mMusicTime;

        ItemHolder(View itemView) {
            super(itemView);
            mMusicCover = itemView.findViewById(R.id.music_cover);
            mMusicName = itemView.findViewById(R.id.music_name);
            mMusicTitle = itemView.findViewById(R.id.music_title);
            mMusicTime = itemView.findViewById(R.id.music_time);
            mSongNum = itemView.findViewById(R.id.song_num);
        }
    }

}
