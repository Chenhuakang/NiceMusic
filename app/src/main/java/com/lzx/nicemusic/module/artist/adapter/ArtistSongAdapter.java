package com.lzx.nicemusic.module.artist.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzx.musiclibrary.aidl.listener.OnPlayerEventListener;
import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.lzx.musiclibrary.manager.MusicManager;
import com.lzx.musiclibrary.manager.TimerTaskManager;
import com.lzx.musiclibrary.utils.LogUtil;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.utils.FormatUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by xian on 2018/2/15.
 */

public class ArtistSongAdapter extends RecyclerView.Adapter<ArtistSongAdapter.ArtistHolder> {
    private Context mContext;
    private List<SongInfo> mSongInfoList;
    private OnItemClickListener mOnItemClickListener;

    public ArtistSongAdapter(Context context) {
        mContext = context;
        mSongInfoList = new ArrayList<>();
    }

    public void setSongInfoList(List<SongInfo> songInfoList) {
        mSongInfoList = songInfoList;
        notifyDataSetChanged();
    }

    public List<SongInfo> getSongInfoList() {
        return mSongInfoList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public ArtistHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist_song, parent, false);
        return new ArtistHolder(view);
    }

    TimerTaskManager manager;

    public void onRemoveUpdateProgressTask(){
        if (manager!=null){
            manager.onRemoveUpdateProgressTask();
            manager = null;
        }
    }

    @Override
    public void onBindViewHolder(ArtistHolder holder, int position) {
        SongInfo info = mSongInfoList.get(position);
        holder.mMusicNum.setText(String.valueOf(position + 1));
        holder.mMusicName.setText(info.getSongName());
        AnimationDrawable animationDrawable = (AnimationDrawable) holder.mImageAnim.getDrawable();

        manager = new TimerTaskManager();
        if (MusicManager.isCurrMusicIsPlayingMusic(info)) {
            holder.mMusicNum.setVisibility(View.GONE);
            holder.mImageAnim.setVisibility(View.VISIBLE);
            if (MusicManager.isPlaying()) {
                animationDrawable.start();
                manager.scheduleSeekBarUpdate();
            } else {
                animationDrawable.stop();
                manager.stopSeekBarUpdate();
            }
        } else {
            animationDrawable.stop();
            manager.onRemoveUpdateProgressTask();
            manager = null;
            holder.mMusicNum.setVisibility(View.VISIBLE);
            holder.mImageAnim.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(view -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(info, position);
            }
        });

        if (manager!=null) {
            manager.setUpdateProgressTask(() -> updateProgress(info, holder));
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateProgress(SongInfo songInfo, ArtistHolder holder) {
        long progress = MusicManager.get().getProgress();
        if (MusicManager.isCurrMusicIsPlayingMusic(songInfo)) {
            if (MusicManager.isPlaying()) {
                holder.mTime.setText(FormatUtil.formatMusicTime(progress));
            } else {
                holder.mTime.setText("00:00");
            }
        } else {
            holder.mTime.setText("00:00");
        }
    }

    @Override
    public int getItemCount() {
        return mSongInfoList.size();
    }


    class ArtistHolder extends RecyclerView.ViewHolder {

        TextView mMusicNum, mMusicName, mTime;
        ImageView mImageAnim;

        ArtistHolder(View itemView) {
            super(itemView);
            mMusicNum = itemView.findViewById(R.id.song_num);
            mMusicName = itemView.findViewById(R.id.song_name);
            mImageAnim = itemView.findViewById(R.id.image_anim);
            mTime = itemView.findViewById(R.id.time);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(SongInfo info, int position);
    }
}
