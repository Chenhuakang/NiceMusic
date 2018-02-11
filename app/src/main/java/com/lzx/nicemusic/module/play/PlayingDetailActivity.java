package com.lzx.nicemusic.module.play;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;

import com.lzx.musiclibrary.aidl.listener.OnPlayerEventListener;
import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.lzx.musiclibrary.manager.MusicManager;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpActivity;
import com.lzx.nicemusic.base.mvp.factory.CreatePresenter;
import com.lzx.nicemusic.bean.LrcInfo;
import com.lzx.nicemusic.constans.Constans;
import com.lzx.nicemusic.module.play.presenter.PlayContract;
import com.lzx.nicemusic.module.play.presenter.PlayPresenter;
import com.plattysoft.leonids.ParticleSystem;

import java.util.Random;

/**
 * Created by xian on 2018/1/21.
 */
@CreatePresenter(PlayPresenter.class)
public class PlayingDetailActivity extends BaseMvpActivity<PlayContract.View, PlayPresenter> implements PlayContract.View, OnPlayerEventListener {

    private SongInfo mMusicInfo;
    private PlayingUIController mUIController;
    private SongListReceiver mSongListReceiver;
    private ParticleSystem ps;

    public static void launch(Context context, SongInfo info) {
        Intent intent = new Intent(context, PlayingDetailActivity.class);
        intent.putExtra("SongInfo", info);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_playing_detail;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mMusicInfo = getIntent().getParcelableExtra("SongInfo");

        mUIController = new PlayingUIController(this, mMusicInfo);
        mUIController.initViews();
        mUIController.initialization();

        //注册广播
        mSongListReceiver = new SongListReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constans.ACTION_STOP_MUSIC);
        registerReceiver(mSongListReceiver, filter);

        //获取歌词
        getPresenter().getLrcInfo(mMusicInfo.getSongId());

        MusicManager.get().addPlayerEventListener(this);
    }


    @Override
    public void onMusicSwitch(SongInfo music) {
        getPresenter().getLrcInfo(music.getSongId());
        mUIController.updateUI(music);
    }

    @Override
    public void onPlayerStart() {
        mUIController.onPlayerStart();
    }

    @Override
    public void onPlayerPause() {
        mUIController.onPlayerPause();
    }

    @Override
    public void onPlayCompletion() {
        mUIController.onPlayCompletion();
    }

    @Override
    public void onError(String errorMsg) {
        mUIController.onPlayCompletion();
    }

    @Override
    public void onBuffering(boolean isFinishBuffer) {

    }

    @Override
    public void onLrcInfoSuccess(LrcInfo info) {
        mUIController.initLrcView(info);
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
                // Create a particle system and start emiting
                ps = new ParticleSystem(this, 100, starArray[new Random().nextInt(starArray.length)], 800);
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
    public void onBackPressed() {
        if (mUIController.isPlayListVisible()) {
            mUIController.hidePlayListLayout();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUIController.onDestroy();
        unregisterReceiver(mSongListReceiver);
        MusicManager.get().removePlayerEventListener(this);
    }
}
