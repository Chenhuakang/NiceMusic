package com.lzx.nicemusic.module.play;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lzx.musiclibrary.TimerTaskManager;
import com.lzx.musiclibrary.aidl.listener.OnPlayerEventListener;
import com.lzx.musiclibrary.aidl.model.MusicInfo;
import com.lzx.musiclibrary.manager.MusicManager;
import com.lzx.musiclibrary.playback.State;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpActivity;
import com.lzx.nicemusic.base.mvp.factory.CreatePresenter;
import com.lzx.nicemusic.bean.SingerInfo;
import com.lzx.nicemusic.helper.PlayHelper;
import com.lzx.nicemusic.module.play.presenter.PlayContract;
import com.lzx.nicemusic.module.play.presenter.PlayPresenter;
import com.lzx.nicemusic.utils.DisplayUtil;
import com.lzx.nicemusic.utils.FormatUtil;
import com.lzx.nicemusic.utils.GlideUtil;
import com.lzx.nicemusic.widget.OuterLayerImageView;

/**
 * Created by xian on 2018/1/21.
 */
@CreatePresenter(PlayPresenter.class)
public class PlayingDetailActivity extends BaseMvpActivity<PlayContract.View, PlayPresenter> implements PlayContract.View, OnPlayerEventListener {

    private MusicInfo mMusicInfo;
    private PlayingUIController mUIController;

    public static void launch(Context context, MusicInfo info) {
        Intent intent = new Intent(context, PlayingDetailActivity.class);
        intent.putExtra("MusicInfo", info);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_playing_detail;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mMusicInfo = getIntent().getParcelableExtra("MusicInfo");

        mUIController = new PlayingUIController(this, mMusicInfo);
        mUIController.initViews();
        mUIController.initialization();

        MusicManager.get().addPlayerEventListener(this);
    }

    @Override
    public void onSingerInfoSuccess(SingerInfo singerInfo) {
    }

    @Override
    public void onMusicChange(MusicInfo music) {

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
    public void onPlayerStop() {
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
        MusicManager.get().removePlayerEventListener(this);
    }
}
