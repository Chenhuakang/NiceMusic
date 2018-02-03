package com.lzx.nicemusic.module.main.sectioned;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzx.nicemusic.R;
import com.lzx.nicemusic.module.play.PlayingDetailActivity;
import com.lzx.nicemusic.utils.FormatUtil;
import com.lzx.nicemusic.utils.GlideUtil;
import com.lzx.nicemusic.widget.OuterLayerImageView;

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

    private int[] icons = new int[]{
            R.drawable.ic_category_t3, R.drawable.ic_category_t4,
            R.drawable.ic_category_t13, R.drawable.ic_category_t21,
            R.drawable.ic_category_t22, R.drawable.ic_category_t26,
            R.drawable.ic_category_t28, R.drawable.ic_category_t29,
            R.drawable.ic_category_t31, R.drawable.ic_category_promo
    };

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
        setTypeIcon(holder);
    }

    private void setTypeIcon(HeaderHolder headerViewHolder) {
        switch (title) {
            case "新歌榜":
                headerViewHolder.mIcType.setImageResource(icons[9]);
                break;
            case "热歌榜":
                headerViewHolder.mIcType.setImageResource(icons[1]);
                break;
            case "摇滚榜":
                headerViewHolder.mIcType.setImageResource(icons[2]);
                break;
            case "爵士":
                headerViewHolder.mIcType.setImageResource(icons[3]);
                break;
            case "流行":
                headerViewHolder.mIcType.setImageResource(icons[4]);
                break;
            case "欧美金曲榜":
                headerViewHolder.mIcType.setImageResource(icons[5]);
                break;
            case "经典老歌榜":
                headerViewHolder.mIcType.setImageResource(icons[6]);
                break;
            case "情歌对唱榜":
                headerViewHolder.mIcType.setImageResource(icons[7]);
                break;
            case "影视金曲榜":
                headerViewHolder.mIcType.setImageResource(icons[8]);
                break;
            case "网络歌曲榜":
                headerViewHolder.mIcType.setImageResource(icons[0]);
                break;
        }
    }

    @Override
    public void onBindFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        super.onBindFooterViewHolder(viewHolder);
        FooterHolder holder = (FooterHolder) viewHolder;
        holder.mItemDynamic.setText("换一换");
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ItemHolder holder = (ItemHolder) viewHolder;
        MusicInfo musicInfo = mMusicInfoList.get(position);
        GlideUtil.loadImageByUrl(mContext, musicInfo.musicCover, holder.mMusicCover);
        holder.mMusicTitle.setText(musicInfo.musicTitle);
        holder.mPlayCount.setText(FormatUtil.formatNum(String.valueOf(musicInfo.playCount)));
        holder.mLikeNum.setText(FormatUtil.formatNum(String.valueOf(musicInfo.favorites)));
        holder.mMusicTime.setText(FormatUtil.formatMusicTime(musicInfo.musicDuration));
        holder.mAlbumName.setText(musicInfo.musicArtist + "·" + musicInfo.albumTitle);
        holder.itemView.setOnClickListener(view -> PlayingDetailActivity.launch(mContext, musicInfo));
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {
        TextView mItemType;
        ImageView mIcType;

        public HeaderHolder(View itemView) {
            super(itemView);
            mItemType = itemView.findViewById(R.id.item_type_tv);
            mIcType = itemView.findViewById(R.id.ic_type);
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
        OuterLayerImageView mMusicCover;
        TextView mMusicTitle, mPlayCount, mLikeNum, mAlbumName, mMusicTime;

        public ItemHolder(View itemView) {
            super(itemView);
            mMusicCover = itemView.findViewById(R.id.music_cover);
            mMusicTitle = itemView.findViewById(R.id.music_title);
            mPlayCount = itemView.findViewById(R.id.play_count);
            mLikeNum = itemView.findViewById(R.id.like_num);
            mAlbumName = itemView.findViewById(R.id.album_name);
            mMusicTime = itemView.findViewById(R.id.music_time);
        }
    }


}
