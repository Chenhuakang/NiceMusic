package com.lzx.nicemusic.module.songlist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzx.musiclibrary.aidl.model.MusicInfo;
import com.lzx.musiclibrary.manager.MusicManager;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.utils.FormatUtil;
import com.lzx.nicemusic.utils.GlideUtil;
import com.lzx.nicemusic.widget.adapter.BaseViewHolder;
import com.lzx.nicemusic.widget.adapter.LoadMoreAdapter;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by xian on 2018/2/5.
 */

public class SongListAdapter extends LoadMoreAdapter<MusicInfo> implements Observer {

    private Context mContext;

    private OnItemClickListener mItemClickListener;

    public SongListAdapter(Context context) {
        super(context);
        mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    @Override
    public BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_song_list, parent, false);
        return new ItemHolder(view);
    }

    @Override
    protected void BindViewHolder(BaseViewHolder viewHolder, int position) {
        ItemHolder holder = (ItemHolder) viewHolder;
        MusicInfo musicInfo = mDataList.get(position);
        holder.mSongNum.setText(String.valueOf((position + 1)));
        holder.mMusicName.setText(musicInfo.musicTitle);
        holder.mMusicTime.setText(FormatUtil.formatMusicTime(musicInfo.musicDuration));
        holder.mMusicTitle.setText(musicInfo.albumArtist + " · " + musicInfo.albumTitle);
        GlideUtil.loadImageByUrl(mContext, musicInfo.musicCover, holder.mMusicCover);
        if (MusicManager.isCurrMusicIsPlayingMusic(musicInfo)) {
            holder.mImgVolume.setVisibility(View.VISIBLE);
            holder.mSongNum.setVisibility(View.INVISIBLE);
        }else {
            holder.mImgVolume.setVisibility(View.INVISIBLE);
            holder.mSongNum.setVisibility(View.VISIBLE);
        }
        holder.itemView.setOnClickListener(view -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(musicInfo, position);
            }
        });
    }

    @Override
    public void update(Observable observable, Object arg) {
        int msg = (int) arg;
        if (msg == MusicManager.MSG_PLAYER_START || msg == MusicManager.MSG_PLAYER_PAUSE) {
            notifyDataSetChanged();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(MusicInfo musicInfo, int position);
    }

    @Override
    protected int getViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    class ItemHolder extends BaseViewHolder {
        ImageView mMusicCover,mImgVolume;
        TextView mSongNum, mMusicName, mMusicTitle, mMusicTime;

        ItemHolder(View itemView) {
            super(itemView, mContext, false);
            mMusicCover = $(R.id.music_cover);
            mMusicName = $(R.id.music_name);
            mMusicTitle = $(R.id.music_title);
            mMusicTime = $(R.id.music_time);
            mSongNum = $(R.id.song_num);
            mImgVolume = $(R.id.img_volume);
        }


    }
}
