package com.lzx.nicemusic.module.play.sectioned;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lzx.musiclibrary.aidl.model.MusicInfo;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.db.DbManager;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by xian on 2018/2/4.
 */

public class DialogMusicListSectioned extends StatelessSection {

    private Context mContext;
    private DbManager mDbManager;
    private List<MusicInfo> mMusicInfos;

    public DialogMusicListSectioned(Context context) {
        super(new SectionParameters.Builder(R.layout.section_dialog_item).build());
        mContext = context;
        mDbManager = new DbManager(context);
        mDbManager.AsyQueryPlayList().subscribe(list -> mMusicInfos = list);
    }

    @Override
    public int getContentItemsTotal() {
        return mMusicInfos != null ? mMusicInfos.size() : 0;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ItemHolder holder = (ItemHolder) viewHolder;
        MusicInfo info = mMusicInfos.get(position);
        holder.mMusicTitle.setText(info.musicTitle);
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        TextView mMusicTitle;

        public ItemHolder(View itemView) {
            super(itemView);
            mMusicTitle = itemView.findViewById(R.id.item_music_name);
        }
    }
}
