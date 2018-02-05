package com.lzx.nicemusic.module.songlist;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.lzx.musiclibrary.aidl.model.MusicInfo;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpActivity;
import com.lzx.nicemusic.base.mvp.factory.CreatePresenter;
import com.lzx.nicemusic.module.songlist.presenter.SongListContract;
import com.lzx.nicemusic.module.songlist.presenter.SongListPresenter;
import com.lzx.nicemusic.module.songlist.sectioned.SongListSectioned;
import com.lzx.nicemusic.utils.DisplayUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * 歌单列表
 *
 * @author lzx
 * @date 2018/2/5
 */
@CreatePresenter(SongListPresenter.class)
public class SongListActivity extends BaseMvpActivity<SongListContract.View, SongListPresenter> implements SongListContract.View {

    private SmartRefreshLayout mSmartRefreshLayout;
    private CoordinatorLayout mCoordinatorLayout;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private SectionedRecyclerViewAdapter mAdapter;
    private String title;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_song_list;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mSmartRefreshLayout = findViewById(R.id.refreshLayout);
        mCoordinatorLayout = findViewById(R.id.main_content);
        mAppBarLayout = findViewById(R.id.app_bar_layout);
        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        mToolbar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.recycle_view);
        mFloatingActionButton = findViewById(R.id.fab);

        title = getIntent().getStringExtra("title");

        initToolBar();
        initRecyclerView();
        initFloatingActionButton();
        initSmartRefreshLayout();
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
        mAdapter = new SectionedRecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initFloatingActionButton() {
        mFloatingActionButton.setClickable(false);
        mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B3B3B3")));
        mFloatingActionButton.setTranslationY(-DisplayUtil.dip2px(this, 32));
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initSmartRefreshLayout() {
        mSmartRefreshLayout.setEnableAutoLoadmore(false);
        mSmartRefreshLayout.setEnableRefresh(false);
        mSmartRefreshLayout.setEnableLoadmore(true);
        mSmartRefreshLayout.setOnLoadmoreListener(refreshlayout -> {
            getPresenter().loadMoreSongList(title);
            refreshlayout.finishLoadmore();
        });
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
    public void onGetSongListSuccess(List<MusicInfo> list) {
        mAdapter.addSection(new SongListSectioned(this, list));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadMoreSongListSuccess(List<MusicInfo> list) {
        mAdapter.addSection(new SongListSectioned(this, list));
        mAdapter.notifyDataSetChanged();
    }
}
