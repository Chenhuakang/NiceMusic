package com.lzx.nicemusic.module.songlist;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.lzx.musiclibrary.aidl.model.MusicInfo;
import com.lzx.musiclibrary.manager.MusicManager;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpActivity;
import com.lzx.nicemusic.base.mvp.factory.CreatePresenter;
import com.lzx.nicemusic.module.songlist.adapter.SongListAdapter;
import com.lzx.nicemusic.module.songlist.presenter.SongListContract;
import com.lzx.nicemusic.module.songlist.presenter.SongListPresenter;
import com.lzx.nicemusic.utils.DisplayUtil;
import com.lzx.nicemusic.utils.GlideUtil;
import com.lzx.nicemusic.widget.OuterLayerImageView;

import java.util.List;


/**
 * 歌单列表
 *
 * @author lzx
 * @date 2018/2/5
 */
@CreatePresenter(SongListPresenter.class)
public class SongListActivity extends BaseMvpActivity<SongListContract.View, SongListPresenter> implements SongListContract.View {

    private CoordinatorLayout mCoordinatorLayout;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private OuterLayerImageView mAlbumCover;
    private SongListAdapter mAdapter;
    private String title;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_song_list;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mCoordinatorLayout = findViewById(R.id.main_content);
        mAppBarLayout = findViewById(R.id.app_bar_layout);
        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        mToolbar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.recycle_view);
        mFloatingActionButton = findViewById(R.id.fab);
        mAlbumCover = findViewById(R.id.album_cover);

        title = getIntent().getStringExtra("title");

        initToolBar();
        initRecyclerView();
        initFloatingActionButton();
        initSmartRefreshLayout();
        initAlbumCover();

        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> setViewsTranslation(verticalOffset));

        getPresenter().requestSongList(title);
    }

    private void initToolBar() {
        mToolbar.setTitle(title);
        setSupportActionBar(mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        //设置还没收缩时状态下字体颜色
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        //设置收缩后Toolbar上字体的颜色
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SongListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initFloatingActionButton() {
        mFloatingActionButton.setClickable(true);
        mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
        mFloatingActionButton.setTranslationY(-DisplayUtil.dip2px(this, 32));
        mFloatingActionButton.setOnClickListener(v -> {
            List<MusicInfo> musicInfos = mAdapter.getMusicInfos();
            MusicManager.get().playMusic(musicInfos, 0);
        });
    }

    private void initSmartRefreshLayout() {
        mAdapter.setOnLoadMoreListener(() -> getPresenter().loadMoreSongList(title));
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

    private void initAlbumCover() {
        GlideUtil.loadImageByUrl(this, getPresenter().getAlbumCover(title), mAlbumCover);
    }

    @Override
    public void onGetSongListSuccess(List<MusicInfo> list) {
        mAdapter.setMusicInfos(list, false);
        mAdapter.setShowLoadMore(list.size() >= getPresenter().size);
    }

    @Override
    public void loadMoreSongListSuccess(List<MusicInfo> list) {
        mAdapter.setMusicInfos(list, true);
        mAdapter.setShowLoadMore(list.size() >= getPresenter().size);
    }

    @Override
    public void loadFinishAllData() {
        mAdapter.setShowLoadMore(false);
        mAdapter.showLoadAllDataUI();
        Toast.makeText(mContext, "没有更多了", Toast.LENGTH_SHORT).show();
    }
}
