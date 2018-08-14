package com.lzx.nicemusic.module.artist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzx.musiclibrary.aidl.listener.OnPlayerEventListener;
import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.lzx.musiclibrary.constans.PlayMode;
import com.lzx.musiclibrary.helper.QueueHelper;
import com.lzx.musiclibrary.manager.MusicManager;
import com.lzx.musiclibrary.utils.LogUtil;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpActivity;
import com.lzx.nicemusic.base.mvp.factory.CreatePresenter;
import com.lzx.nicemusic.bean.SingerInfo;
import com.lzx.nicemusic.module.artist.adapter.ArtistSongAdapter;
import com.lzx.nicemusic.module.artist.presenter.ArtistContract;
import com.lzx.nicemusic.module.artist.presenter.ArtistPresenter;
import com.lzx.nicemusic.module.play.PlayingDetailActivity;
import com.lzx.nicemusic.utils.FormatUtil;
import com.lzx.nicemusic.utils.GlideUtil;
import com.lzx.nicemusic.widget.CircleImageView;

import java.util.List;

/**
 * @author lzx
 * @date 2018/2/14
 */
@CreatePresenter(ArtistPresenter.class)
public class ArtistDetailActivity extends BaseMvpActivity<ArtistContract.View, ArtistPresenter> implements ArtistContract.View, OnPlayerEventListener {

    public static void launch(Context context, SongInfo info) {
        Intent intent = new Intent(context, ArtistDetailActivity.class);
        intent.putExtra("songInfo", info);
        context.startActivity(intent);
    }

    private SongInfo mSongInfo;
    private TextView mSongName, mArtistName, mArtistDesc, mTextPeopleNum, mTextPlayNum;
    private ImageView mSongCover;
    private CircleImageView mArtistCover;
    private FloatingActionButton mFloatingActionButton;
    private RecyclerView mRecyclerView;
    private NestedScrollView mNestedScrollView;
    private RelativeLayout mCoverLayout, mArtistLayout;
    private AppBarLayout mAppBarLayout;
    private ArtistSongAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_artist;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mSongInfo = getIntent().getParcelableExtra("songInfo");
        mSongName = findViewById(R.id.song_name);
        mArtistName = findViewById(R.id.artist_name);
        mSongCover = findViewById(R.id.song_cover);
        mArtistCover = findViewById(R.id.artist_cover);
        mArtistDesc = findViewById(R.id.artist_desc);
        mFloatingActionButton = findViewById(R.id.fab);
        mRecyclerView = findViewById(R.id.recycle_view);
        mNestedScrollView = findViewById(R.id.scrollView);
        mCoverLayout = findViewById(R.id.cover_layout);
        mArtistLayout = findViewById(R.id.artist_layout);
        mAppBarLayout = findViewById(R.id.app_bar_layout);
        mTextPeopleNum = findViewById(R.id.text_people_num);
        mTextPlayNum = findViewById(R.id.text_play_num);

        initUI(mSongInfo);
        mAdapter = new ArtistSongAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setAdapter(mAdapter);
        getPresenter().getArtistSongs(mSongInfo.getArtistId());
        getPresenter().getArtistInfo(mSongInfo.getArtistId());

        MusicManager.get().addPlayerEventListener(this);

        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> setViewsTranslation(verticalOffset));
        mFloatingActionButton.setOnClickListener(view -> {
            List<SongInfo> songInfos = mAdapter.getSongInfoList();
            int position = 0;
            if (songInfos.size() > 0 && songInfos.contains(mSongInfo)) {
                position = QueueHelper.getMusicIndexOnQueue(songInfos, mSongInfo.getSongId());
            } else {
                songInfos.add(mSongInfo);
            }
            MusicManager.get().playMusic(songInfos, position);
            PlayingDetailActivity.launch(mContext, songInfos, position);
        });
        mAdapter.setOnItemClickListener((info, position) -> {
            List<SongInfo> songInfos = mAdapter.getSongInfoList();
            if (songInfos.size() > 0) {
                MusicManager.get().playMusic(songInfos, position);
            } else {
                MusicManager.get().playMusicByInfo(info);
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("name", "大妈的");
        MusicManager.get().updateNotificationContentIntent(bundle, null);


        findViewById(R.id.btn_pre).setOnClickListener(v -> {
            if (MusicManager.get().hasPre()) {
                MusicManager.get().playPre();
            }
        });
        findViewById(R.id.btn_next).setOnClickListener(v -> {
            if (MusicManager.get().hasNext()) {
                MusicManager.get().playNext();
            }
        });
        findViewById(R.id.btn_play_mode).setOnClickListener(v -> {
            int mode = MusicManager.get().getPlayMode();
            if (mode == PlayMode.PLAY_IN_LIST_LOOP) {
                MusicManager.get().setPlayMode(PlayMode.PLAY_IN_ORDER);
                Toast.makeText(mContext, "顺序播放", Toast.LENGTH_SHORT).show();
            } else if (mode == PlayMode.PLAY_IN_ORDER) {
                MusicManager.get().setPlayMode(PlayMode.PLAY_IN_SINGLE_LOOP);
                Toast.makeText(mContext, "单曲循环", Toast.LENGTH_SHORT).show();
            } else if (mode == PlayMode.PLAY_IN_SINGLE_LOOP) {
                MusicManager.get().setPlayMode(PlayMode.PLAY_IN_RANDOM);
                Toast.makeText(mContext, "随机播放", Toast.LENGTH_SHORT).show();
            } else if (mode == PlayMode.PLAY_IN_RANDOM) {
                MusicManager.get().setPlayMode(PlayMode.PLAY_IN_LIST_LOOP);
                Toast.makeText(mContext, "列表循环", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUI(SongInfo info) {
        mSongName.setText(info.getSongName());
        mArtistName.setText(info.getArtist());
        mTextPeopleNum.setText(FormatUtil.formatNum(String.valueOf(info.getFavorites())));
        mTextPlayNum.setText(FormatUtil.formatNum(String.valueOf(info.getDuration())));
        GlideUtil.loadImageByUrl(this, info.getSongCover(), mSongCover);
    }

    private void setViewsTranslation(int target) {
        mFloatingActionButton.setTranslationY(target);
        if (target == 0) {
            showFAB();
        } else if (target < 0) {
            hideFAB();
        }
    }

    private void showFAB() {
        mFloatingActionButton.animate().scaleX(1f).scaleY(1f)
                .setInterpolator(new OvershootInterpolator())
                .start();
        mFloatingActionButton.setClickable(true);
    }

    private void hideFAB() {
        mFloatingActionButton.animate().scaleX(0f).scaleY(0f)
                .setInterpolator(new AccelerateInterpolator())
                .start();
        mFloatingActionButton.setClickable(false);
    }

    @Override
    public void onArtistSongsSuccess(List<SongInfo> list) {
        mAdapter.setSongInfoList(list);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onArtistInfoSuccess(SingerInfo info) {
        GlideUtil.loadImageByUrl(this, info.getAvatar(), mArtistCover);
        mArtistName.setText(info.getNickname());
        String desc = !TextUtils.isEmpty(info.getCompany()) ? "." + info.getCompany() : "";
        mArtistDesc.setText(info.getSongsTotal() + " Song" + desc);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.onRemoveUpdateProgressTask();
        MusicManager.get().removePlayerEventListener(this);
    }

    @Override
    public void onMusicSwitch(SongInfo music) {
        mSongInfo = music;
        initUI(music);
    }

    @Override
    public void onPlayerStart() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPlayerPause() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPlayCompletion() {

    }

    @Override
    public void onPlayerStop() {

    }

    @Override
    public void onError(String errorMsg) {
        Toast.makeText(mContext, "播放失败!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAsyncLoading(boolean isFinishLoading) {

    }
}
