package com.lzx.nicemusic.module.play;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lzx.musiclibrary.bean.MusicInfo;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpActivity;
import com.lzx.nicemusic.base.mvp.factory.CreatePresenter;
import com.lzx.nicemusic.bean.SingerInfo;
import com.lzx.nicemusic.helper.PlayHelper;
import com.lzx.nicemusic.module.play.presenter.PlayContract;
import com.lzx.nicemusic.module.play.presenter.PlayPresenter;
import com.lzx.nicemusic.utils.GlideUtil;
import com.lzx.nicemusic.widget.OuterLayerImageView;

/**
 * Created by xian on 2018/1/21.
 */
@CreatePresenter(PlayPresenter.class)
public class PlayingDetailActivity extends BaseMvpActivity<PlayContract.View, PlayPresenter> implements PlayContract.View, View.OnClickListener {

    private TextView mMusicName, mNickname, mAlbumName, mCountry, mSingerDesc;
    private OuterLayerImageView mMusicCover;
    private ImageView mAvatar, mBtnMusicList, mBtnPlayMode, mBtnPlayPause, mBtnPre, mBtnNext;
    private Button mBtnAllMusic;
    private SeekBar mSeekBar;
    private MusicInfo mMusicInfo;

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
        mMusicName = findViewById(R.id.music_name);
        mMusicCover = findViewById(R.id.music_cover);
        mAvatar = findViewById(R.id.avatar);
        mNickname = findViewById(R.id.nickname);
        mAlbumName = findViewById(R.id.album_name);
        mCountry = findViewById(R.id.country);
        mBtnAllMusic = findViewById(R.id.btn_all_music);
        mSingerDesc = findViewById(R.id.singer_desc);
        mBtnMusicList = findViewById(R.id.btn_music_list);
        mBtnPlayMode = findViewById(R.id.btn_play_mode);
        mBtnPlayPause = findViewById(R.id.btn_play_pause);
        mBtnPre = findViewById(R.id.btn_pre);
        mBtnNext = findViewById(R.id.btn_next);
        mSeekBar = findViewById(R.id.seekBar);

        mBtnAllMusic.setOnClickListener(this);
        mBtnMusicList.setOnClickListener(this);
        mBtnPlayMode.setOnClickListener(this);
        mBtnPlayPause.setOnClickListener(this);
        mBtnPre.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);

        mMusicName.setText(mMusicInfo.musicTitle);
        GlideUtil.loadImageByUrl(this, mMusicInfo.musicCover, mMusicCover);
        getPresenter().requestSingerInfo(mMusicInfo.artistId);


    }

    @Override
    public void onSingerInfoSuccess(SingerInfo singerInfo) {
        GlideUtil.loadImageByUrl(this, singerInfo.getAvatar(), mAvatar);
        mNickname.setText(mMusicInfo.musicArtist);
        mAlbumName.setText(mMusicInfo.albumTitle);
        mCountry.setText(singerInfo.getCountry());
        mSingerDesc.setText(singerInfo.getIntro());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_all_music:
                break;
            case R.id.btn_music_list:
                break;
            case R.id.btn_play_mode:
                break;
            case R.id.btn_play_pause:
                PlayHelper.playMusic(this, mMusicInfo);
                break;
            case R.id.btn_pre:
                break;
            case R.id.btn_next:
                break;
        }
    }
}
