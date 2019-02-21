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

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.activities.model.MainViewModel;
import com.lzx.nicemusic.bean.Personalized;
import com.lzx.nicemusic.utils.InjectorUtils;
import com.lzx.nicemusic.utils.LogUtil;
import com.lzx.nicemusic.utils.Utils;
import com.lzx.nicemusic.view.ProgressView;
import com.lzx.nicemusic.view.RecyclerViewAdapter;
import com.lzx.starrysky.manager.MediaSessionConnection;
import com.lzx.starrysky.manager.MusicManager;
import com.lzx.starrysky.manager.OnPlayerEventListener;
import com.lzx.starrysky.model.SongInfo;
import com.lzx.starrysky.utils.TimerTaskManager;

import java.util.List;


public class MainActivity extends PlayerActivity implements OnPlayerEventListener {

    private ImageView mCoverView;
    private View mTitleView;
    private TextView mTimeView;
    private TextView mDurationView;
    private ProgressView mProgressView;
    private View mFabView;
    private TextView mSongTitle, mArtist, mName, mCounter;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;

    private MainViewModel mViewModel;

    private TimerTaskManager mTimerTaskManager;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_list);
        mCoverView = findViewById(R.id.cover);
        mTitleView = findViewById(R.id.title);
        mTimeView = findViewById(R.id.time);
        mDurationView = findViewById(R.id.duration);
        mProgressView = findViewById(R.id.progress);
        mFabView = findViewById(R.id.fab);
        mSongTitle = findViewById(R.id.song_title);
        mArtist = findViewById(R.id.artist);
        mName = findViewById(R.id.name);
        mCounter = findViewById(R.id.counter);

        mRecyclerView = findViewById(R.id.tracks);

        MediaSessionConnection.getInstance(this).connect();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerViewAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.onItemClickListener() {
            @Override
            public void onItemClick(Personalized personalized, int position) {
                updatePlayList(personalized, position);
            }

            @Override
            public void onItemClick(List<SongInfo> list, int position) {
                MusicManager.getInstance().playMusicByInfo(list.get(position));
            }
        });

        mViewModel = ViewModelProviders
                .of(this, InjectorUtils.provideMainActivityViewModel(this))
                .get(MainViewModel.class);

        mTimerTaskManager = new TimerTaskManager();

        MusicManager.getInstance().addPlayerEventListener(this);

        mTimerTaskManager.setUpdateProgressTask(() -> {
            long position = MusicManager.getInstance().getPlayingPosition();
            long duration = MusicManager.getInstance().getDuration() / 1000;
            if (mProgressView.getMax() != duration) {
                mProgressView.setMax((int) duration);
                mDurationView.setText(Utils.formatMusicTime(duration));
            }
            mProgressView.setProgress((int) position);
            mTimeView.setText(Utils.formatMusicTime(position));
        });

        mViewModel.requestPersonalized()
                .subscribe(highQualities -> {
                    mAdapter.setPersonalizeds(highQualities);
                }, Throwable::printStackTrace);
    }

    @SuppressLint("CheckResult")
    public void updatePlayList(Personalized personalized, int position) {
        mViewModel.requestPlayListDetail(personalized.id)
                .subscribe(songInfos -> {
                    if (songInfos.size() > 0) {
                        MusicManager.getInstance().updatePlayList(songInfos);
                        mAdapter.setShowPlayList(true);
                        mAdapter.setSongInfos(songInfos, false);
                        SongInfo songInfo = songInfos.get(0);
                        mName.setText(songInfo.getAlbumName());
                        mCounter.setText("By:" + songInfo.getAlbumArtist());
                    }
                }, Throwable::printStackTrace);
    }

    public void onFabClick(View view) {
        if (MusicManager.getInstance().getPlayList().size() == 0) {
            SongInfo songInfo = new SongInfo();
            songInfo.setSongId("30431376");
            songInfo.setSongName("易燃易爆炸");
            songInfo.setArtist("陈粒");
            songInfo.setAlbumName("如也");
            songInfo.setDuration(200000);
            songInfo.setSongCover("http://img.jammyfm.com/wordpress/wp-content/uploads/2017/07/201707261110447854.jpg");
            songInfo.setSongUrl("http://music.163.com/song/media/outer/url?id=" + songInfo.getSongId() + ".mp3");
            MusicManager.getInstance().playMusicByInfo(songInfo);
        }
        //noinspection unchecked
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                new Pair<>(mCoverView, ViewCompat.getTransitionName(mCoverView)),
                new Pair<>(mTitleView, ViewCompat.getTransitionName(mTitleView)),
                new Pair<>(mTimeView, ViewCompat.getTransitionName(mTimeView)),
                new Pair<>(mDurationView, ViewCompat.getTransitionName(mDurationView)),
                new Pair<>(mProgressView, ViewCompat.getTransitionName(mProgressView)),
                new Pair<>(mFabView, ViewCompat.getTransitionName(mFabView)));
        ActivityCompat.startActivity(this, new Intent(this, DetailActivity.class), options.toBundle());
    }

    @Override
    public void onMusicSwitch(SongInfo songInfo) {
        LogUtil.i("= onMusicSwitch = " + songInfo.getSongName());
        Glide.with(this)
                .load(songInfo.getSongCover())
                .into(mCoverView);
        mSongTitle.setText(songInfo.getSongName());
        mArtist.setText(songInfo.getArtist());
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
        Toast.makeText(this, "播放失败", Toast.LENGTH_SHORT).show();
        LogUtil.i("errorCode = " + errorCode + " errorMsg = " + errorMsg);
    }

    @Override
    public void onBackPressed() {
        if (mAdapter.isShowPlayList()) {
            mAdapter.setShowPlayList(false);
            mName.setText("精品歌单");
            mCounter.setText("属于你的歌单");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimerTaskManager.removeUpdateProgressTask();
        MusicManager.getInstance().removePlayerEventListener(this);
    }
}
