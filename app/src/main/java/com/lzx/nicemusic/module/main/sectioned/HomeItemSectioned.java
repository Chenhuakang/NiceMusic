package com.lzx.nicemusic.module.main.sectioned;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzx.nicemusic.R;
import com.lzx.nicemusic.lib.bean.MusicInfo;
import com.lzx.nicemusic.utils.GlideUtil;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by xian on 2018/1/20.
 */

public class HomeItemSectioned extends StatelessSection {
    private Context mContext;
    private List<MusicInfo> mMusicInfoList;
    private String title;

    public HomeItemSectioned(Context context, List<MusicInfo> list, String title) {
        super(new SectionParameters.Builder(R.layout.section_home_item)
                .headerResourceId(R.layout.section_home_header)
                .footerResourceId(R.layout.section_home_footer).build());
        mContext = context;
        mMusicInfoList = list;
        this.title = title;
    }

    @Override
    public int getContentItemsTotal() {
        return mMusicInfoList.size();
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getFooterViewHolder(View view) {
        return new FooterHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder) {
        super.onBindHeaderViewHolder(viewHolder);
        HeaderHolder holder = (HeaderHolder) viewHolder;
        holder.mItemType.setText(title);
    }

    @Override
    public void onBindFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        super.onBindFooterViewHolder(viewHolder);
        FooterHolder holder = (FooterHolder) viewHolder;
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ItemHolder holder = (ItemHolder) viewHolder;
        MusicInfo musicInfo = mMusicInfoList.get(position);
        GlideUtil.loadImageByUrl(mContext, musicInfo.musicCover, holder.mMusicCover);
        holder.mMusicTitle.setText(musicInfo.musicTitle);
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {
        TextView mItemType;

        public HeaderHolder(View itemView) {
            super(itemView);
            mItemType = itemView.findViewById(R.id.item_type_tv);
        }
    }

    static class FooterHolder extends RecyclerView.ViewHolder {
        Button mBtnMore;
        LinearLayout mRefreshLayout;
        TextView mItemDynamic;
        ImageView mBtnRefresh;

        public FooterHolder(View itemView) {
            super(itemView);
            mBtnMore = itemView.findViewById(R.id.item_btn_more);
            mRefreshLayout = itemView.findViewById(R.id.item_refresh_layout);
            mItemDynamic = itemView.findViewById(R.id.item_dynamic);
            mBtnRefresh = itemView.findViewById(R.id.item_btn_refresh);
        }
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        ImageView mMusicCover;
        TextView mMusicTitle;

        public ItemHolder(View itemView) {
            super(itemView);
            mMusicCover = itemView.findViewById(R.id.music_cover);
            mMusicTitle = itemView.findViewById(R.id.music_title);
        }
    }


}
