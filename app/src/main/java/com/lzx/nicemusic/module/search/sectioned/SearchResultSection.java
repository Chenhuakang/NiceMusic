package com.lzx.nicemusic.module.search.sectioned;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lzx.nicemusic.R;
import com.lzx.nicemusic.module.search.presenter.SearchPresenter;
import com.lzx.nicemusic.utils.GlideUtil;
import com.lzx.nicemusic.widget.SquareImageView;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by xian on 2018/1/14.
 */

public class SearchResultSection extends StatelessSection {

    private Context mContext;
    private List<MusicInfo> mMusicInfos = new ArrayList<>();
    private SearchPresenter mSearchPresenter;

    public SearchResultSection(Context context, List<MusicInfo> infoList,SearchPresenter presenter) {
        super(new SectionParameters.Builder(R.layout.section_search_result)
                .headerResourceId(R.layout.section_search_result_header).build());
        mContext = context;
        mMusicInfos = infoList;
        mSearchPresenter = presenter;
    }

    @Override
    public int getContentItemsTotal() {
        return mMusicInfos.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ResultHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ResultHolder holder = (ResultHolder) viewHolder;
        MusicInfo musicInfo = mMusicInfos.get(position);
        GlideUtil.loadImageByUrl(mContext, musicInfo.albumCover, holder.mMusicCover);
        holder.mMusicName.setText(musicInfo.musicTitle);
        holder.mAlbumName.setText(musicInfo.musicArtist);
    }

    class ResultHolder extends RecyclerView.ViewHolder {
        SquareImageView mMusicCover;
        TextView mMusicName, mAlbumName;

        public ResultHolder(View itemView) {
            super(itemView);
            mMusicCover = itemView.findViewById(R.id.music_cover);
            mMusicName = itemView.findViewById(R.id.music_name);
            mAlbumName = itemView.findViewById(R.id.album_name);
        }
    }
}
