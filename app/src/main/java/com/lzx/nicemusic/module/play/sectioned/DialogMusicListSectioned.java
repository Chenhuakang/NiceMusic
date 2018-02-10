package com.lzx.nicemusic.module.play.sectioned;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzx.musiclibrary.aidl.model.MusicInfo;
import com.lzx.musiclibrary.manager.MusicManager;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.db.DbManager;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by xian on 2018/2/4.
 */

public class DialogMusicListSectioned extends StatelessSection implements Observer {

    private Context mContext;
    private DbManager mDbManager;
    private List<MusicInfo> mMusicInfos;
    private OnDeleteClickListener mOnDeleteClickListener;
    private OnNotifyListener mOnNotifyListener;

    public DialogMusicListSectioned(Context context) {
        super(new SectionParameters.Builder(R.layout.section_dialog_item).build());
        mContext = context;
        mDbManager = new DbManager(context);
        mDbManager.AsyQueryPlayList().subscribe(list -> mMusicInfos = list);
    }

    public DbManager getDbManager() {
        return mDbManager;
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
        holder.mMusicTitle.setText(info.musicTitle + "(" + info.albumTitle + ")" + " - " + info.musicArtist);
        AnimationDrawable animationDrawable = (AnimationDrawable) holder.mImageAnim.getDrawable();
        if (MusicManager.isCurrMusicIsPlayingMusic(info)) {
            holder.mMusicTitle.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            holder.mImageAnim.setVisibility(View.VISIBLE);
            if (MusicManager.isPlaying()) {
                animationDrawable.start();
            } else {
                animationDrawable.stop();
            }
        } else {
            holder.mMusicTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color));
            animationDrawable.stop();
            holder.mImageAnim.setVisibility(View.INVISIBLE);
        }
        holder.mBtnDelete.setOnClickListener(v -> {
            if (mOnDeleteClickListener != null) {
                mOnDeleteClickListener.onDelete(info, position);
            }
        });
        holder.itemView.setOnClickListener(v -> MusicManager.get().playMusicByInfo(info));
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        mOnDeleteClickListener = onDeleteClickListener;
    }

    public void setNotifyListener(OnNotifyListener notifyListener) {
        mOnNotifyListener = notifyListener;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (mOnNotifyListener != null) {
            mOnNotifyListener.notifyAdapter((Integer) arg);
        }
    }

    public interface OnDeleteClickListener {
        void onDelete(MusicInfo info, int position);
    }

    public interface OnNotifyListener {
        void notifyAdapter(Integer msg);
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        TextView mMusicTitle;
        ImageView mBtnDelete, mImageAnim;

        ItemHolder(View itemView) {
            super(itemView);
            mMusicTitle = itemView.findViewById(R.id.item_music_name);
            mBtnDelete = itemView.findViewById(R.id.btn_delete);
            mImageAnim = itemView.findViewById(R.id.image_anim);
        }
    }
}
