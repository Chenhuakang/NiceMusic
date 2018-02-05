package com.lzx.nicemusic.module.main.sectioned;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lzx.nicemusic.R;
import com.lzx.nicemusic.module.songlist.SongListActivity;
import com.lzx.nicemusic.widget.OuterLayerImageView;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * @author lzx
 * @date 2018/2/5
 */

public class MainItemSectioned extends StatelessSection {
    private Context mContext;
    private String[] songListArray = new String[]{
            "我的歌单",
            "新歌榜",
            "热歌榜",
            "摇滚榜",
            "爵士",
            "流行",
            "欧美金曲榜",
            "经典老歌榜",
            "情歌对唱榜",
            "影视金曲榜",
            "网络歌曲榜"
    };

    public MainItemSectioned(Context context) {
        super(new SectionParameters.Builder(R.layout.section_main_song_list).build());
        mContext = context;

    }

    @Override
    public int getContentItemsTotal() {
        return songListArray.length;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ItemHolder holder = (ItemHolder) viewHolder;
        String title = songListArray[position];
        holder.mAlbumTitle.setText(title);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, SongListActivity.class);
            intent.putExtra("title", title);
            mContext.startActivity(intent);
        });
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        OuterLayerImageView mAlbumCover;
        TextView mAlbumTitle, mAlbumDesc, mAlbumSongNum;

        ItemHolder(View itemView) {
            super(itemView);
            mAlbumCover = itemView.findViewById(R.id.album_cover);
            mAlbumTitle = itemView.findViewById(R.id.album_name);
            mAlbumDesc = itemView.findViewById(R.id.album_desc);
            mAlbumSongNum = itemView.findViewById(R.id.album_song_num);
        }
    }
}
