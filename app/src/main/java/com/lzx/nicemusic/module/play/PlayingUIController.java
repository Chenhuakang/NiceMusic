package com.lzx.nicemusic.module.play;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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

import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.lzx.musiclibrary.constans.PlayMode;
import com.lzx.musiclibrary.manager.MusicManager;
import com.lzx.musiclibrary.manager.TimerTaskManager;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.bean.LrcAnalysisInfo;
import com.lzx.nicemusic.bean.LrcInfo;
import com.lzx.nicemusic.db.DbManager;
import com.lzx.nicemusic.module.play.adapter.DialogMusicListAdapter;
import com.lzx.nicemusic.module.play.adapter.TimerAdapter;
import com.lzx.nicemusic.utils.DisplayUtil;
import com.lzx.nicemusic.utils.FormatUtil;
import com.lzx.nicemusic.utils.GlideUtil;
import com.lzx.nicemusic.widget.OuterLayerImageView;
import com.lzx.nicemusic.widget.SimpleProgress;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private TextView mLyricsText;
    private SimpleProgress mSimpleProgress;
    private List<LrcAnalysisInfo> lrcList;

    private RecyclerView mRecyclerView;
    private View mPlayDarkBg;
    private DialogMusicListAdapter mDialogMusicListAdapter;
    private TimerAdapter mTimerAdapter;

    private RelativeLayout.LayoutParams params;
    private int phoneHeight;

    private ObjectAnimator mCoverAnim;
    private ObjectAnimator mPlayListAnim;
    private ObjectAnimator mPlayDrakBgAnim;
    private long currentPlayTime = 0;
    private TimerTaskManager mTimerTaskManager;
    private SongInfo mMusicInfo;
    private DbManager mDbManager;
    private AppCompatActivity mActivity;
    private Context mContext;

    PlayingUIController(AppCompatActivity activity, SongInfo musicInfo) {
        mMusicInfo = musicInfo;
        mActivity = activity;
        mContext = mActivity.getApplicationContext();
        mDbManager = new DbManager(activity);
        mTimerTaskManager = new TimerTaskManager();
        mTimerTaskManager.setUpdateProgressTask(this::updateProgress);
    }

    public PlayingUIController(AppCompatActivity activity) {
        mActivity = activity;
        mContext = mActivity.getApplicationContext();
        mDbManager = new DbManager(activity);
        mTimerTaskManager = new TimerTaskManager();
        mTimerTaskManager.setUpdateProgressTask(this::updateProgress);
    }

    void initViews() {
//        mSeekBar = mActivity.findViewById(R.id.seekBar);
//        mTotalTime = mActivity.findViewById(R.id.total_time);
//        mBtnPlayPause = mActivity.findViewById(R.id.btn_play_pause);
//        mStartTime = mActivity.findViewById(R.id.start_time);
//        mMusicName = mActivity.findViewById(R.id.music_name);
//        mMusicBg = mActivity.findViewById(R.id.music_bg);
//        mMusicCover = mActivity.findViewById(R.id.music_cover);
//        mSongerName = mActivity.findViewById(R.id.songer_name);
//        mBtnMusicList = mActivity.findViewById(R.id.btn_music_list);
//        mBtnPlayTime = mActivity.findViewById(R.id.btn_play_time);
//        mBtnPre = mActivity.findViewById(R.id.btn_pre);
//        mBtnNext = mActivity.findViewById(R.id.btn_next);

        mBtnMusicList.setOnClickListener(this);
        mBtnPlayTime.setOnClickListener(this);
        mBtnPre.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mBtnPlayPause.setOnClickListener(this);
        initPlayListLayout();
        initMusicCoverAnim();
    }

    public void initPlayListLayout() {
        mPlayListLayout = mActivity.findViewById(R.id.play_list_layout);
        mRecyclerView = mActivity.findViewById(R.id.recycle_view);
        mBtnDismiss = mActivity.findViewById(R.id.btn_dismiss);
        mPlayDarkBg = mActivity.findViewById(R.id.play_dark_bg);
        mBtnPlayMode = mActivity.findViewById(R.id.btn_play_mode);
        mLyricsText = mActivity.findViewById(R.id.lyrics_text);
        mBtnPlayMode.setOnClickListener(this);
        mBtnDismiss.setOnClickListener(this);
        mPlayDarkBg.setOnClickListener(this);
        phoneHeight = DisplayUtil.getPhoneHeight(mContext);
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, phoneHeight * 6 / 10);
        mPlayListLayout.setLayoutParams(params);
        mDialogMusicListAdapter = new DialogMusicListAdapter(mContext);
        mTimerAdapter = new TimerAdapter(mContext);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        MusicManager.get().addStateObservable(mDialogMusicListAdapter);
    }

    public DbManager getDbManager() {
        return mDbManager;
    }

    public void initSimpleProgressBar() {
        mSimpleProgress = mActivity.findViewById(R.id.simple_progress);
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

    public void starUpdateProgress() {
        mTimerTaskManager.scheduleSeekBarUpdate();
    }

    public void pauseUpdateProgress() {
        mTimerTaskManager.stopSeekBarUpdate();
    }

    void updateUI(SongInfo info) {
        mMusicInfo = info;
        resetCoverAnim();
        mMusicName.setText(info.getSongName());
        mSongerName.setText(info.getArtist());
        if (mLyricsText != null) {
            mLyricsText.setText(info.getArtist());
        }
        GlideUtil.loadImageByUrl(mContext, info.getSongCover(), mMusicCover);
        GlideUtil.loadBlurImage(mContext, info.getSongCover(), mMusicBg);
        mSeekBar.setProgress(0);
        mSeekBar.setMax((int) info.getDuration());
        mTotalTime.setText(FormatUtil.formatMusicTime(info.getDuration()));
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
    public void showPlayListLayout(boolean isSongList) {
        if (!isSongList) {
            mBtnPlayMode.setVisibility(View.GONE);
            mRecyclerView.setAdapter(mTimerAdapter);
            initPlayListAnim(true, phoneHeight, phoneHeight * 4 / 10);
        } else {
            mBtnPlayMode.setVisibility(View.VISIBLE);
            mRecyclerView.setAdapter(mDialogMusicListAdapter);
            mDbManager.asyQueryPlayList()
                    .subscribe(infoList -> {
                        if (infoList == null || infoList.size() == 0) {
                            return;
                        }
                        int index = -1;
                        List<SongInfo> list = mDialogMusicListAdapter.getMusicInfos();
                        for (int i = 0; i < list.size(); i++) {
                            SongInfo info = list.get(i);
                            if (MusicManager.isCurrMusicIsPlayingMusic(info)) {
                                index = i;
                                break;
                            }
                        }
                        if (index != -1) {
                            mRecyclerView.scrollToPosition(index);
                        }

                        int playMode = MusicManager.get().getPlayMode();
                        if (playMode == PlayMode.PLAY_IN_LIST_LOOP) {
                            mBtnPlayMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_repeat_black_24dp, 0, 0, 0);
                            mBtnPlayMode.setText("顺序播放");
                        } else if (playMode == PlayMode.PLAY_IN_RANDOM) {
                            mBtnPlayMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_shuffle_black_24dp, 0, 0, 0);
                            mBtnPlayMode.setText("随机播放");
                        } else if (playMode == PlayMode.PLAY_IN_SINGLE_LOOP) {
                            mBtnPlayMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_repeat_one_black_24dp, 0, 0, 0);
                            mBtnPlayMode.setText("单曲循环");
                        }

                        initPlayListAnim(true, phoneHeight, phoneHeight * 4 / 10);
                    }, throwable -> {
                        Toast.makeText(mActivity, "打开列表失败", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    /**
     * 隐藏播放列表
     */
    public void hidePlayListLayout() {
        initPlayListAnim(false, phoneHeight * 4 / 10, phoneHeight);
    }

    /**
     * 列表是否在显示
     */
    public boolean isPlayListVisible() {
        return mPlayListLayout.getVisibility() == View.VISIBLE;
    }

    /**
     * 更新进度
     */
    private void updateProgress() {
        long progress = MusicManager.get().getProgress();
        if (mSeekBar != null && mStartTime != null) {
            mSeekBar.setProgress((int) progress);
            mStartTime.setText(FormatUtil.formatMusicTime(progress));
        }
        if (mSimpleProgress != null) {
            if (mMusicInfo == null) {
                mMusicInfo = MusicManager.get().getCurrPlayingMusic();
            }
            if (mSimpleProgress.getMax() == -1) {
                mSimpleProgress.setMax(mMusicInfo.getDuration());
            }
            mSimpleProgress.setProgress(progress);
        }
        if (lrcList != null && lrcList.size() > 0) {
            mLyricsText.setText(getLrc(progress));
        }
    }

    private String getLrc(long progress) {
        String lrc = "";
        for (int i = 0; i < lrcList.size(); i++) {
            int index = i + 1;
            if (index >= lrcList.size() - 1) {
                index = lrcList.size() - 1;
            }
            if (progress >= lrcList.get(i).getTime() && progress < lrcList.get(index).getTime()) {
                lrc = lrcList.get(i).getText();
                break;
            }
        }
        return lrc;
    }

    @SuppressLint("UseSparseArrays")
    private Map<Long, String> lrcMap = new HashMap<>();

    public void initLrcView(LrcInfo info) {
        mLyricsText.setVisibility(View.VISIBLE);
        lrcList = LrcAnalysisInfo.parseLrcString(info.getLrcContent());
        if (lrcList != null) {
            for (LrcAnalysisInfo lrcAnalysisInfo : lrcList) {
                lrcMap.put(lrcAnalysisInfo.getTime(), lrcAnalysisInfo.getText());
            }
        }
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
//        switch (v.getId()) {
//            case R.id.btn_music_list:
//
//                showPlayListLayout(true);
//                break;
//            case R.id.btn_play_time:
//                mRecyclerView.setAdapter(mTimerAdapter);
//                showPlayListLayout(false);
//                break;
//            case R.id.btn_play_pause:
//                MusicManager.get().playMusicByInfo(mMusicInfo);
//                break;
//            case R.id.btn_pre:
//                MusicManager.get().playPre();
//                break;
//            case R.id.btn_next:
//                MusicManager.get().playNext();
//                break;
//            case R.id.btn_play_mode:
//                int playMode = MusicManager.get().getPlayMode();
//                if (playMode == PlayMode.PLAY_IN_LIST_LOOP) {
//                    MusicManager.get().setPlayMode(PlayMode.PLAY_IN_RANDOM);
//                    mBtnPlayMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_shuffle_black_24dp, 0, 0, 0);
//                    mBtnPlayMode.setText("随机播放");
//                } else if (playMode == PlayMode.PLAY_IN_RANDOM) {
//                    MusicManager.get().setPlayMode(PlayMode.PLAY_IN_SINGLE_LOOP);
//                    mBtnPlayMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_repeat_one_black_24dp, 0, 0, 0);
//                    mBtnPlayMode.setText("单曲循环");
//                } else if (playMode == PlayMode.PLAY_IN_SINGLE_LOOP) {
//                    MusicManager.get().setPlayMode(PlayMode.PLAY_IN_LIST_LOOP);
//                    mBtnPlayMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_repeat_black_24dp, 0, 0, 0);
//                    mBtnPlayMode.setText("顺序播放");
//                }
//                break;
//            case R.id.btn_dismiss:
//                hidePlayListLayout();
//                break;
//            case R.id.play_dark_bg:
//                hidePlayListLayout();
//                break;
//        }
    }
}
