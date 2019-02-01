/*
 * Copyright (c) 2016. André Mion
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lzx.nicemusic.activities;

import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.transition.Transition;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.utils.Utils;
import com.lzx.nicemusic.view.ProgressView;
import com.lzx.nicemusic.view.TransitionAdapter;
import com.lzx.nicemusic.view.musiccoverview.MusicCoverView;
import com.lzx.starrysky.manager.MusicManager;
import com.lzx.starrysky.manager.OnPlayerEventListener;
import com.lzx.starrysky.model.SongInfo;
import com.lzx.starrysky.utils.TimerTaskManager;

public class DetailActivity extends PlayerActivity implements MusicCoverView.Callbacks, OnPlayerEventListener, View.OnClickListener {

    private MusicCoverView mCoverView;
    private TextView mSongTitle, mArtist;
    private TextView mTimeView;
    private TextView mDurationView;
    private ImageView mRepeatView, mShuffleView, mPrevious, mRewind, mForward, mNext;
    private ProgressView mProgressView;
    private TimerTaskManager mTimerTaskManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_detail);

        mProgressView = findViewById(R.id.progress);
        mCoverView = findViewById(R.id.cover);
        mSongTitle = findViewById(R.id.song_title);
        mArtist = findViewById(R.id.artist);
        mTimeView = findViewById(R.id.time);
        mDurationView = findViewById(R.id.duration);
        mRepeatView = findViewById(R.id.repeat);
        mShuffleView = findViewById(R.id.shuffle);
        mPrevious = findViewById(R.id.previous);
        mRewind = findViewById(R.id.rewind);
        mForward = findViewById(R.id.forward);
        mNext = findViewById(R.id.next);

        mTimerTaskManager = new TimerTaskManager();

        SongInfo songInfo = MusicManager.getInstance().getNowPlayingSongInfo();
        updateUI(songInfo);

        mCoverView.setCallbacks(this);

        getWindow().getSharedElementEnterTransition().addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                MusicManager.getInstance().playMusic();
                mCoverView.start();
            }
        });
        MusicManager.getInstance().addPlayerEventListener(this);

        mTimerTaskManager.setUpdateProgressTask(() -> {
            long position = MusicManager.getInstance().getPlayingPosition();
            long duration = MusicManager.getInstance().getDuration() / 1000;
            if (mProgressView.getMax() != duration) {
                mProgressView.setMax((int) duration);
                mDurationView.setText(Utils.formatMusicTime(duration));
            }
            mProgressView.setProgress((int) position);
            mDurationView.setText(Utils.formatMusicTime(duration));
            mTimeView.setText(Utils.formatMusicTime(position));
        });

        if (MusicManager.getInstance().isPlaying()) {
            mTimerTaskManager.startToUpdateProgress();
        }

        mRepeatView.setOnClickListener(this);
        mShuffleView.setOnClickListener(this);
        mPrevious.setOnClickListener(this);
        mRewind.setOnClickListener(this);
        mForward.setOnClickListener(this);
        mNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.repeat:
                int repeatMode = MusicManager.getInstance().getRepeatMode();
                if (repeatMode == PlaybackStateCompat.REPEAT_MODE_NONE) {

                    MusicManager.getInstance().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
                    Toast.makeText(this, "设置为单曲循环", Toast.LENGTH_SHORT).show();
                    mRepeatView.setImageResource(R.drawable.ic_repeat_one_white_24dp);

                } else if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ONE) {
                    MusicManager.getInstance().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
                    Toast.makeText(this, "设置为列表循环", Toast.LENGTH_SHORT).show();
                    mRepeatView.setImageResource(R.drawable.ic_repeat_white_24dp);

                } else if (repeatMode == PlaybackStateCompat.REPEAT_MODE_ALL) {
                    MusicManager.getInstance().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
                    mRepeatView.setImageResource(R.drawable.ic_mode_none_white_24dp);
                    Toast.makeText(this, "设置为列表播放", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.shuffle:
                int shuffleMode = MusicManager.getInstance().getShuffleMode();

                if (shuffleMode == PlaybackStateCompat.SHUFFLE_MODE_NONE) {
                    MusicManager.getInstance().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_ALL);
                    mShuffleView.setImageResource(R.drawable.ic_shuffle_white_24dp);
                    Toast.makeText(this, "设置为随机播放", Toast.LENGTH_SHORT).show();

                } else {
                    MusicManager.getInstance().setShuffleMode(PlaybackStateCompat.SHUFFLE_MODE_NONE);
                    mShuffleView.setImageResource(R.drawable.ic_repeat_all_white_24dp);
                    Toast.makeText(this, "设置为顺序播放", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.previous:
                if (MusicManager.getInstance().isSkipToPreviousEnabled()) {
                    MusicManager.getInstance().skipToPrevious();
                } else {
                    Toast.makeText(this, "没有上一首了", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rewind:
                MusicManager.getInstance().rewind();
                break;
            case R.id.forward:
                MusicManager.getInstance().fastForward();
                break;
            case R.id.next:
                if (MusicManager.getInstance().isSkipToNextEnabled()) {
                    MusicManager.getInstance().skipToNext();
                } else {
                    Toast.makeText(this, "没有下一首了", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void updateUI(SongInfo songInfo) {
        if (songInfo != null) {
            Glide.with(this).load(songInfo.getSongCover()).into(mCoverView);
            mSongTitle.setText(songInfo.getSongName());
            mArtist.setText(songInfo.getArtist());
        } else {
            mCoverView.setImageResource(R.drawable.album_cover_daft_punk);
        }
    }

    @Override
    public void onMorphEnd(MusicCoverView coverView) {
        // Nothing to do
    }

    @Override
    public void onRotateEnd(MusicCoverView coverView) {
        supportFinishAfterTransition();
    }

    public void onFabClick(View view) {
       // MusicManager.getInstance().pauseMusic();
        mCoverView.stop();
    }

    @Override
    public void onBackPressed() {
        onFabClick(null);
    }

    @Override
    public void onMusicSwitch(SongInfo songInfo) {
        updateUI(songInfo);
    }

    @Override
    public void onPlayerStart() {
        mTimerTaskManager.startToUpdateProgress();
    }

    @Override
    public void onPlayerPause() {
        mTimerTaskManager.stopToUpdateProgress();
    }

    @Override
    public void onPlayerStop() {
        mTimerTaskManager.stopToUpdateProgress();
    }

    @Override
    public void onPlayCompletion(SongInfo songInfo) {
        mTimerTaskManager.stopToUpdateProgress();
    }

    @Override
    public void onBuffering() {

    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        mTimerTaskManager.stopToUpdateProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicManager.getInstance().removePlayerEventListener(this);
        mTimerTaskManager.removeUpdateProgressTask();
    }


}
