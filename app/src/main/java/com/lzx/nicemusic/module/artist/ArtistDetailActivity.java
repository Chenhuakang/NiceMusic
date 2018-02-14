package com.lzx.nicemusic.module.artist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpActivity;
import com.lzx.nicemusic.base.mvp.factory.CreatePresenter;
import com.lzx.nicemusic.module.artist.presenter.ArtistContract;
import com.lzx.nicemusic.module.artist.presenter.ArtistPresenter;
import com.lzx.nicemusic.utils.GlideUtil;

import java.util.List;

/**
 * @author lzx
 * @date 2018/2/14
 */
@CreatePresenter(ArtistPresenter.class)
public class ArtistDetailActivity extends BaseMvpActivity<ArtistContract.View, ArtistPresenter> implements ArtistContract.View {

    public static void launch(Context context, SongInfo info) {
        Intent intent = new Intent(context, ArtistDetailActivity.class);
        intent.putExtra("SongInfo", info);
        context.startActivity(intent);
    }

    private SongInfo mSongInfo;
    private TextView mSongName, mArtistName, mArtistDesc;
    private ImageView mSongCover, mArtistCover;
    private FloatingActionButton mFloatingActionButton;
    private RecyclerView mRecyclerView;
    private NestedScrollView mNestedScrollView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_artist;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mSongInfo = getIntent().getParcelableExtra("SongInfo");
        mSongName = findViewById(R.id.song_name);
        mArtistName = findViewById(R.id.artist_name);
        mSongCover = findViewById(R.id.song_cover);
        mArtistCover = findViewById(R.id.artist_cover);
        mArtistDesc = findViewById(R.id.artist_desc);
        mFloatingActionButton = findViewById(R.id.fab);
        mRecyclerView = findViewById(R.id.recycle_view);
        mNestedScrollView = findViewById(R.id.scrollView);

        mSongName.setText(mSongInfo.getSongName());
        mArtistName.setText(mSongInfo.getArtist());
        GlideUtil.loadImageByUrl(this, mSongInfo.getSongCover(), mSongCover);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setNestedScrollingEnabled(false);
        getPresenter().getArtistSongs(mSongInfo.getArtistId());
    }

    @Override
    public void onArtistSongsSuccess(List<SongInfo> list) {

    }
}
