package com.lzx.nicemusic.module.play;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lzx.musiclibrary.aidl.listener.OnPlayerEventListener;
import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.lzx.musiclibrary.cache.MusicMd5Generator;
import com.lzx.musiclibrary.constans.PlayMode;
import com.lzx.musiclibrary.manager.MusicManager;
import com.lzx.musiclibrary.manager.TimerTaskManager;
import com.lzx.musiclibrary.utils.LogUtil;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpActivity;
import com.lzx.nicemusic.base.mvp.factory.CreatePresenter;
import com.lzx.nicemusic.bean.LrcAnalysisInfo;
import com.lzx.nicemusic.bean.LrcInfo;
import com.lzx.nicemusic.constans.Constans;
import com.lzx.nicemusic.listener.SimpleSeekBarChangeListener;
import com.lzx.nicemusic.module.play.presenter.PlayContract;
import com.lzx.nicemusic.module.play.presenter.PlayPresenter;
import com.lzx.nicemusic.utils.FormatUtil;
import com.lzx.nicemusic.utils.GlideUtil;
import com.lzx.nicemusic.widget.CircleImageView;
import com.plattysoft.leonids.ParticleSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xian on 2018/1/21.
 */
@CreatePresenter(PlayPresenter.class)
public class PlayingDetailActivity extends BaseMvpActivity<PlayContract.View, PlayPresenter> implements PlayContract.View, OnPlayerEventListener, View.OnClickListener {

    private int position;
    private List<SongInfo> songInfos;
    private SongInfo mSongInfo;
    private ParticleSystem ps;
    private ObjectAnimator mCoverAnim;
    private long currentPlayTime = 0;
    private TimerTaskManager mTimerTaskManager;
    private List<LrcAnalysisInfo> lrcList;

    private TextView mSongName, mStartTime, mTotalTime, mTextLyrics;
    private CircleImageView mMusicCover;
    private ImageView mBlueBg, mBtnPlayPause, mBtnPre, mBtnNext;
    private SeekBar mSeekBar;


    public static void launch(Context context, List<SongInfo> songInfos, int position) {
        Intent intent = new Intent(context, PlayingDetailActivity.class);
        intent.putParcelableArrayListExtra("SongInfos", (ArrayList<? extends Parcelable>) songInfos);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_playing_detail;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        songInfos = getIntent().getParcelableArrayListExtra("SongInfos");
        position = getIntent().getIntExtra("position", position);
        mSongInfo = songInfos.get(position);

        mSongName = findViewById(R.id.song_name);
        mMusicCover = findViewById(R.id.music_cover);
        mBlueBg = findViewById(R.id.blue_bg);
        mBtnPlayPause = findViewById(R.id.btn_play_pause);
        mBtnPre = findViewById(R.id.btn_pre);
        mBtnNext = findViewById(R.id.btn_next);
        mSeekBar = findViewById(R.id.seekBar);
        mStartTime = findViewById(R.id.start_time);
        mTotalTime = findViewById(R.id.total_time);
        mTextLyrics = findViewById(R.id.text_lyrics);

        mBtnPlayPause.setOnClickListener(this);
        mBtnPre.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(new SimpleSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                super.onStopTrackingTouch(seekBar);
                MusicManager.get().seekTo(seekBar.getProgress());
            }
        });
        MusicManager.get().addPlayerEventListener(this);

        mTimerTaskManager = new TimerTaskManager();
        mTimerTaskManager.setUpdateProgressTask(this::updateProgress);

        if (mSongInfo != null) {
            getPresenter().getLrcInfo(mSongInfo.getSongId());
        }
        updateUI(mSongInfo);
        initMusicCoverAnim();
        if (MusicManager.isPaused()) {
            MusicManager.get().resumeMusic();
        }


        findViewById(R.id.shuiji).setOnClickListener(v -> {
            Toast.makeText(mContext, "随机播放", Toast.LENGTH_SHORT).show();
            MusicManager.get().setPlayMode(PlayMode.PLAY_IN_RANDOM);
        });
        findViewById(R.id.shunxu).setOnClickListener(v -> {
            Toast.makeText(mContext, "顺序播放", Toast.LENGTH_SHORT).show();
            MusicManager.get().setPlayMode(PlayMode.PLAY_IN_ORDER);
        });
        findViewById(R.id.shangyishou).setOnClickListener(v -> {
            SongInfo songInfo = MusicManager.get().getPreMusic();
            Toast.makeText(mContext, "上一首信息 = " + songInfo.getSongName(), Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.xiayishou).setOnClickListener(v -> {
            SongInfo songInfo = MusicManager.get().getNextMusic();
            Toast.makeText(mContext, "下一首信息 = " + songInfo.getSongName(), Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.tingzhi).setOnClickListener(v -> {
            Toast.makeText(mContext, "停止", Toast.LENGTH_SHORT).show();
            MusicManager.get().stopMusic();
            MusicManager.get().stopNotification();
        });


    }

    private void updateUI(SongInfo music) {
        if (music == null) {
            return;
        }
        mSeekBar.setMax((int) music.getDuration());
        mSongName.setText(music.getSongName());
        mTotalTime.setText(FormatUtil.formatMusicTime(music.getDuration()));
        GlideUtil.loadImageByUrl(this, music.getSongCover(), mMusicCover);
        GlideUtil.loadImageByUrl(this, music.getSongCover(), mBlueBg);
    }

    /**
     * 更新进度
     */
    private void updateProgress() {
        long progress = MusicManager.get().getProgress();
        long bufferProgress = MusicManager.get().getBufferedPosition();
        mSeekBar.setProgress((int) progress);
        mSeekBar.setSecondaryProgress((int) bufferProgress);

        //     LogUtil.i("bufferProgress = " + bufferProgress);

        mStartTime.setText(FormatUtil.formatMusicTime(progress));
        if (lrcList != null && lrcList.size() > 0) {
            mTextLyrics.setText(getLrc(progress));
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

    //    private void fetchPictureColor(final Bitmap bitmap, final NotificationCompat.Builder builder) {
//        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
//            @Override
//            public void onGenerated(Palette palette) {
//                Palette.Swatch swatch = palette.getMutedSwatch();
//                if (swatch != null) {
//                    notificationColorCache.put(bitmap, swatch.getRgb());
//                    builder.setColor(swatch.getRgb());
//                    mNotificationManager.notify(NOTIFICATION_ID, builder.build());
//                }
//            }
//        });
//    }

    String[] colors = new String[]{"#fb7299", "#50cdd4", "#000000"};
    int index = 0;

    @Override
    public void onMusicSwitch(SongInfo music) {
        mSongInfo = music;
        getPresenter().getLrcInfo(music.getSongId());
        mBtnPlayPause.setImageResource(R.drawable.ic_play);
        updateUI(music);

    }

    @Override
    public void onPlayerStart() {
        mBtnPlayPause.setImageResource(R.drawable.ic_pause);
        mTimerTaskManager.scheduleSeekBarUpdate();
        startCoverAnim();

    }

    @Override
    public void onPlayerPause() {
        mBtnPlayPause.setImageResource(R.drawable.ic_play);
        mTimerTaskManager.stopSeekBarUpdate();
        pauseCoverAnim();
    }

    @Override
    public void onPlayCompletion() {
        mBtnPlayPause.setImageResource(R.drawable.ic_play);
        mSeekBar.setProgress(0);
        mStartTime.setText("00:00");
        resetCoverAnim();
    }

    @Override
    public void onPlayerStop() {
        Toast.makeText(mContext, "onPlayerStop", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String errorMsg) {
        Toast.makeText(mContext, "播放失败", Toast.LENGTH_SHORT).show();
        resetCoverAnim();
    }

    @Override
    public void onAsyncLoading(boolean isFinishLoading) {

    }

    @Override
    public void onLrcInfoSuccess(LrcInfo info) {
        lrcList = LrcAnalysisInfo.parseLrcString(info.getLrcContent());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_play_pause:
                if (MusicManager.isPlaying()) {
                    MusicManager.get().pauseMusic();
                } else {
                    MusicManager.get().resumeMusic();
                }
                break;
            case R.id.btn_pre:
                if (MusicManager.get().hasPre()) {
                    MusicManager.get().playPre();
                } else {
                    Toast.makeText(mContext, "没有上一首了", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_next:
                if (MusicManager.get().hasNext()) {
                    index++;
                    if (index == colors.length - 1) {
                        index = 0;
                    }
                    MusicManager.get().updateNotificationThemeColor(Color.parseColor(colors[index]));
                    MusicManager.get().playNext();
                } else {
                    Toast.makeText(mContext, "没有下一首了", Toast.LENGTH_SHORT).show();
                }
                break;
        }
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


    private class SongListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && action.equals(Constans.ACTION_STOP_MUSIC)) {
                MusicManager.get().stopMusic();
                PlayingDetailActivity.this.finish();
            }
        }
    }

    private Integer[] starArray = new Integer[]{
            R.drawable.pl_blue,
            R.drawable.pl_red,
            R.drawable.pl_yellow
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int random = (int) (Math.random() * starArray.length);
                ps = new ParticleSystem(this, 100, starArray[random], 800);
                ps.setScaleRange(0.7f, 1.3f);
                ps.setSpeedRange(0.05f, 0.1f);
                ps.setRotationSpeedRange(90, 180);
                ps.setFadeOut(200, new AccelerateInterpolator());
                ps.emit((int) event.getX(), (int) event.getY(), 40);
                break;
            case MotionEvent.ACTION_MOVE:
                ps.updateEmitPoint((int) event.getX(), (int) event.getY());
                break;
            case MotionEvent.ACTION_UP:
                ps.stopEmitting();
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetCoverAnim();
        mCoverAnim = null;
        mTimerTaskManager.onRemoveUpdateProgressTask();
        MusicManager.get().removePlayerEventListener(this);
    }


}
