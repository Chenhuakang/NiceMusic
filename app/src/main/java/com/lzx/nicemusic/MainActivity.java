package com.lzx.nicemusic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzx.musiclibrary.aidl.listener.OnPlayerEventListener;
import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.lzx.musiclibrary.helper.QueueHelper;
import com.lzx.musiclibrary.manager.MusicManager;
import com.lzx.musiclibrary.utils.LogUtil;
import com.lzx.nicemusic.base.BaseMvpActivity;
import com.lzx.nicemusic.base.mvp.factory.CreatePresenter;
import com.lzx.nicemusic.bean.LrcInfo;
import com.lzx.nicemusic.constans.Constans;
import com.lzx.nicemusic.module.main.MainFragment;
import com.lzx.nicemusic.module.play.PlayingDetailActivity;
import com.lzx.nicemusic.module.play.PlayingUIController;
import com.lzx.nicemusic.module.play.presenter.PlayContract;
import com.lzx.nicemusic.module.play.presenter.PlayPresenter;
import com.lzx.nicemusic.module.search.SearchActivity;
import com.lzx.nicemusic.module.songlist.SongListFragment;
import com.lzx.nicemusic.utils.GlideUtil;
import com.lzx.nicemusic.utils.SpUtil;

@CreatePresenter(PlayPresenter.class)
public class MainActivity extends BaseMvpActivity<PlayContract.View, PlayPresenter> implements PlayContract.View, View.OnClickListener, OnPlayerEventListener {

    private ImageView mMusicCover, mBtnPlayList, mBtnPlayPause;
    private TextView mMusicName;

    private Intent intent;
    private MainFragment mMainFragment;
    private SongListFragment mSongListFragment;
    private PlayingUIController mUIController;
    private SongInfo localSongInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mMusicCover = findViewById(R.id.music_cover);
        mMusicName = findViewById(R.id.music_name);
        mBtnPlayList = findViewById(R.id.btn_play_list);
        mBtnPlayPause = findViewById(R.id.btn_play_pause);

        initFragment();

        mUIController = new PlayingUIController(this);
        mUIController.initPlayListLayout();
        mUIController.initSimpleProgressBar();

        String musicId = SpUtil.getInstance().getString(Constans.LAST_PLAYING_MUSIC);
        if (!TextUtils.isEmpty(musicId)) {
            mUIController.getDbManager().asyGetSongInfoById(musicId)
                    .subscribe(songInfo -> {
                        if (songInfo != null) {
                            localSongInfo = songInfo;
                            GlideUtil.loadImageByUrl(MainActivity.this, songInfo.getSongCover(), mMusicCover);
                            mMusicName.setText(songInfo.getSongName());
                        }
                    }, throwable -> {
                        LogUtil.i(throwable.getMessage());
                    });
        }

        mBtnPlayList.setOnClickListener(this);
        mBtnPlayPause.setOnClickListener(this);
        mMusicCover.setOnClickListener(this);
        mMusicName.setOnClickListener(this);
        MusicManager.get().addPlayerEventListener(this);
    }

    private void initFragment() {
        mMainFragment = (MainFragment) MainFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, mMainFragment)
                .show(mMainFragment).commit();
    }

    public void switchFragment(String title) {
        mSongListFragment = (SongListFragment) SongListFragment.newInstance(title);
        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
        trx.setCustomAnimations(
                R.anim.slide_right_in, R.anim.slide_left_out,
                R.anim.slide_left_in, R.anim.slide_right_out);
        trx.replace(R.id.container, mSongListFragment).addToBackStack(null).commit();
    }

    @Override
    public void onClick(View view) {
        SongInfo info = MusicManager.get().getCurrPlayingMusic();
        switch (view.getId()) {
            case R.id.ed_search:
                intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_play_list:
                mUIController.showPlayListLayout(true);
                break;
            case R.id.btn_play_pause:
                boolean hasLocalInfo = info == null && localSongInfo != null;
                boolean hasPlayingInfo = info != null;
                if (hasLocalInfo || hasPlayingInfo) {
                    if (MusicManager.isPlaying()) {
                        MusicManager.get().pauseMusic();
                    } else {
                        if (hasLocalInfo) {
                            mUIController.getDbManager().asyQueryPlayList().subscribe(songInfos -> {
                                int index = QueueHelper.getMusicIndexOnQueue(songInfos, localSongInfo.getSongId());
                                MusicManager.get().playMusic(songInfos, index);
                            }, throwable -> {
                                Toast.makeText(mContext, "播放失败", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            MusicManager.get().resumeMusic();
                        }
                    }
                }
                break;
            case R.id.music_cover:
            case R.id.music_name:
                if (info != null) {
                    PlayingDetailActivity.launch(this, info);
                }
                break;
        }
    }

    @Override
    public void onLrcInfoSuccess(LrcInfo info) {
        mUIController.initLrcView(info);
    }

    @Override
    public void onMusicSwitch(SongInfo music) {
        GlideUtil.loadImageByUrl(this, music.getSongCover(), mMusicCover);
        mMusicName.setText(music.getSongName());
        getPresenter().getLrcInfo(music.getSongId());
    }

    @Override
    public void onPlayerStart() {
        mBtnPlayPause.setImageResource(R.drawable.notify_btn_dark_pause_normal);
        mUIController.starUpdateProgress();
    }

    @Override
    public void onPlayerPause() {
        mBtnPlayPause.setImageResource(R.drawable.notify_btn_dark_play_normal);
        mUIController.pauseUpdateProgress();
    }

    @Override
    public void onPlayCompletion() {
        mBtnPlayPause.setImageResource(R.drawable.notify_btn_dark_play_normal);
    }

    @Override
    public void onError(String errorMsg) {
        mBtnPlayPause.setImageResource(R.drawable.notify_btn_dark_play_normal);
        Toast.makeText(mContext, "errorMsg = " + errorMsg, Toast.LENGTH_SHORT).show();
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
        MusicManager.get().removePlayerEventListener(this);
    }


}

