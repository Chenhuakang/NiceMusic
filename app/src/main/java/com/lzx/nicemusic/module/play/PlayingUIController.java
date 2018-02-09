package com.lzx.nicemusic.module.play;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lzx.musiclibrary.aidl.model.MusicInfo;
import com.lzx.musiclibrary.manager.MusicManager;
import com.lzx.musiclibrary.manager.TimerTaskManager;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.module.play.sectioned.DialogMusicListSectioned;
import com.lzx.nicemusic.utils.DisplayUtil;
import com.lzx.nicemusic.utils.FormatUtil;
import com.lzx.nicemusic.utils.GlideUtil;
import com.lzx.nicemusic.widget.OuterLayerImageView;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * Created by xian on 2018/2/4.
 */

public class PlayingUIController implements View.OnClickListener {
    private SeekBar mSeekBar;
    private TextView mTotalTime, mStartTime, mMusicName, mSongerName;
    private ImageView mBtnPlayPause;
    private ImageView mBtnMusicList, mBtnPlayTime, mBtnPre, mBtnNext;
    private OuterLayerImageView mMusicBg;
    private ImageView mMusicCover;
    private RelativeLayout mPlayListLayout;
    private TextView mBtnPlayMode, mBtnDismiss;
    private RecyclerView mRecyclerView;
    private View mPlayDarkBg;
    private SectionedRecyclerViewAdapter mRecyclerViewAdapter;
    private DialogMusicListSectioned mSectioned;

    private RelativeLayout.LayoutParams params;
    private int phoneHeight;

    private ObjectAnimator mCoverAnim;
    private ObjectAnimator mPlayListAnim;
    private ObjectAnimator mPlayDrakBgAnim;
    private long currentPlayTime = 0;
    private TimerTaskManager mTimerTaskManager;
    private MusicInfo mMusicInfo;
    private AppCompatActivity mActivity;
    private Context mContext;

    PlayingUIController(AppCompatActivity activity, MusicInfo musicInfo) {
        mActivity = activity;
        mMusicInfo = musicInfo;
        mContext = mActivity.getApplicationContext();
        mTimerTaskManager = new TimerTaskManager();
        mTimerTaskManager.setUpdateProgressTask(this::updateProgress);
    }

    void initViews() {
        mSeekBar = mActivity.findViewById(R.id.seekBar);
        mTotalTime = mActivity.findViewById(R.id.total_time);
        mBtnPlayPause = mActivity.findViewById(R.id.btn_play_pause);
        mStartTime = mActivity.findViewById(R.id.start_time);
        mMusicName = mActivity.findViewById(R.id.music_name);
        mMusicBg = mActivity.findViewById(R.id.music_bg);
        mMusicCover = mActivity.findViewById(R.id.music_cover);
        mSongerName = mActivity.findViewById(R.id.songer_name);
        mPlayListLayout = mActivity.findViewById(R.id.play_list_layout);
        mBtnPlayMode = mActivity.findViewById(R.id.btn_play_mode);
        mRecyclerView = mActivity.findViewById(R.id.recycle_view);
        mBtnDismiss = mActivity.findViewById(R.id.btn_dismiss);
        mBtnMusicList = mActivity.findViewById(R.id.btn_music_list);
        mBtnPlayTime = mActivity.findViewById(R.id.btn_play_time);
        mBtnPre = mActivity.findViewById(R.id.btn_pre);
        mBtnNext = mActivity.findViewById(R.id.btn_next);
        mPlayDarkBg = mActivity.findViewById(R.id.play_dark_bg);

        mBtnMusicList.setOnClickListener(this);
        mBtnPlayTime.setOnClickListener(this);
        mBtnPre.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mBtnPlayPause.setOnClickListener(this);
        mBtnPlayMode.setOnClickListener(this);
        mBtnDismiss.setOnClickListener(this);
        mPlayDarkBg.setOnClickListener(this);

        initMusicCoverAnim();
    }

    /**
     * 初始化UI
     */
    void initialization() {
        updateUI(mMusicInfo);

        if (MusicManager.isCurrMusicIsPlayingMusic(mMusicInfo)) {
            mStartTime.setText(FormatUtil.formatMusicTime(MusicManager.get().getProgress()));
            mSeekBar.setProgress((int) MusicManager.get().getProgress());
            if (MusicManager.isPaused()) {
                mBtnPlayPause.setImageResource(R.drawable.notify_btn_dark_play_normal);
            } else if (MusicManager.isPlaying()) {
                mBtnPlayPause.setImageResource(R.drawable.notify_btn_dark_pause_normal);
                mTimerTaskManager.scheduleSeekBarUpdate();
                startCoverAnim();
            }
        }

        phoneHeight = DisplayUtil.getPhoneHeight(mContext);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, phoneHeight / 2);
        mPlayListLayout.setLayoutParams(params);
        mRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mSectioned = new DialogMusicListSectioned(mContext);
        mRecyclerViewAdapter.addSection(mSectioned);
        mSectioned.setOnDeleteClickListener((info, position) -> {
            mSectioned.getDbManager().AsyDeleteInfoInPlayList(info)
                    .subscribe(aBoolean -> {
                        mRecyclerViewAdapter.notifyItemRemovedFromSection(mSectioned, position);

                       // MusicManager.get().deleteMusicInfoOnPlayList(info);
                    }, throwable -> {
                        Toast.makeText(mActivity, "删除失败", Toast.LENGTH_SHORT).show();
                    });
        });
        mSectioned.setNotifyListener(msg -> {
            if (msg == MusicManager.MSG_PLAYER_START || msg == MusicManager.MSG_PLAYER_PAUSE) {
                mRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
        MusicManager.get().addStateObservable(mSectioned);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicManager.get().seekTo(seekBar.getProgress());
            }
        });
    }

    void updateUI(MusicInfo info) {
        resetCoverAnim();
        mMusicName.setText(info.musicTitle);
        mSongerName.setText(info.musicArtist);
        GlideUtil.loadImageByUrl(mContext, info.musicCover, mMusicCover);
        GlideUtil.loadBlurImage(mContext, info.musicCover, mMusicBg);
        mSeekBar.setProgress(0);
        mSeekBar.setMax((int) info.musicDuration);
        mTotalTime.setText(FormatUtil.formatMusicTime(info.musicDuration));
    }

    /**
     * 转圈动画
     */
    private void initMusicCoverAnim() {
        mCoverAnim = ObjectAnimator.ofFloat(mMusicCover, "rotation", 0, 359);
        mCoverAnim.setDuration(20000);
        mCoverAnim.setInterpolator(new LinearInterpolator());
        mCoverAnim.setRepeatCount(Integer.MAX_VALUE);
    }

    /**
     * 开始转圈
     */
    private void startCoverAnim() {
        mCoverAnim.start();
        mCoverAnim.setCurrentPlayTime(currentPlayTime);
    }

    /**
     * 停止转圈
     */
    private void pauseCoverAnim() {
        currentPlayTime = mCoverAnim.getCurrentPlayTime();
        mCoverAnim.cancel();
    }

    private void resetCoverAnim() {
        pauseCoverAnim();
        mMusicCover.setRotation(0);
    }

    /**
     * 播放列表动画
     */
    private void initPlayListAnim(boolean isShow, int from, int to) {
        mPlayListAnim = ObjectAnimator.ofFloat(mPlayListLayout, "translationY", from, to);
        mPlayListAnim.setInterpolator(new LinearInterpolator());
        mPlayListAnim.setDuration(300);
        mPlayListAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (isShow) {
                    mPlayListLayout.setVisibility(View.VISIBLE);
                } else {
                    playDarkBgAnim(false, 0.6f, 0.0f);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!isShow) {
                    mPlayListLayout.setVisibility(View.GONE);
                } else {
                    playDarkBgAnim(true, 0.0f, 0.6f);
                }
            }
        });
        mPlayListAnim.start();
    }

    /**
     * 背后阴影动画
     */
    private void playDarkBgAnim(boolean isShow, float from, float to) {
        mPlayDrakBgAnim = ObjectAnimator.ofFloat(mPlayDarkBg, "alpha", from, to);
        mPlayDrakBgAnim.setInterpolator(new LinearInterpolator());
        mPlayDrakBgAnim.setDuration(300);
        mPlayDrakBgAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (isShow) {
                    mPlayDarkBg.setClickable(true);
                    mPlayDarkBg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!isShow) {
                    mPlayDarkBg.setClickable(false);
                    mPlayDarkBg.setVisibility(View.GONE);
                }
            }
        });
        mPlayDrakBgAnim.start();
    }

    /**
     * 显示播放列表
     */
    private void showPlayListLayout() {
        initPlayListAnim(true, phoneHeight, params.height);
    }

    /**
     * 隐藏播放列表
     */
    void hidePlayListLayout() {
        initPlayListAnim(false, params.height, phoneHeight);
    }

    /**
     * 列表是否在显示
     */
    boolean isPlayListVisible() {
        return mPlayListLayout.getVisibility() == View.VISIBLE;
    }

    /**
     * 更新进度
     */
    private void updateProgress() {
        long progress = MusicManager.get().getProgress();
        mSeekBar.setProgress((int) progress);
        mStartTime.setText(FormatUtil.formatMusicTime(progress));
    }

    void onPlayerStart() {
        mTimerTaskManager.scheduleSeekBarUpdate();
        mBtnPlayPause.setImageResource(R.drawable.notify_btn_dark_pause_normal);
        startCoverAnim();
    }

    void onPlayerPause() {
        mTimerTaskManager.stopSeekBarUpdate();
        mBtnPlayPause.setImageResource(R.drawable.notify_btn_dark_play_normal);
        pauseCoverAnim();
    }

    void onPlayCompletion() {
        onPlayerPause();
        mSeekBar.setProgress(0);
        mStartTime.setText("00:00");
    }

    void onDestroy() {
        pauseCoverAnim();
        mCoverAnim = null;
        mPlayListAnim = null;
        mPlayDrakBgAnim = null;
        mTimerTaskManager.onRemoveUpdateProgressTask();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_music_list:
                showPlayListLayout();
                break;
            case R.id.btn_play_time:
                break;
            case R.id.btn_play_pause:
                MusicManager.get().playMusicByInfo(mMusicInfo);
                break;
            case R.id.btn_pre:
                MusicManager.get().playPre();
                break;
            case R.id.btn_next:
                MusicManager.get().playNext();
                break;
            case R.id.btn_play_mode:
                break;
            case R.id.btn_dismiss:
                hidePlayListLayout();
                break;
            case R.id.play_dark_bg:
                hidePlayListLayout();
                break;
        }
    }
}
