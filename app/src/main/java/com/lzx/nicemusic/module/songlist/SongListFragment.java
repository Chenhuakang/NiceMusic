package com.lzx.nicemusic.module.songlist;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.lzx.musiclibrary.aidl.model.MusicInfo;
import com.lzx.musiclibrary.manager.MusicManager;
import com.lzx.musiclibrary.utils.LogUtil;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpFragment;
import com.lzx.nicemusic.base.mvp.factory.CreatePresenter;
import com.lzx.nicemusic.db.DbManager;
import com.lzx.nicemusic.MainActivity;
import com.lzx.nicemusic.module.play.PlayingDetailActivity;
import com.lzx.nicemusic.module.songlist.adapter.SongListAdapter;
import com.lzx.nicemusic.module.songlist.presenter.SongListContract;
import com.lzx.nicemusic.module.songlist.presenter.SongListPresenter;
import com.lzx.nicemusic.utils.DisplayUtil;
import com.lzx.nicemusic.utils.GlideUtil;
import com.lzx.nicemusic.utils.SystemBarHelper;
import com.lzx.nicemusic.widget.OuterLayerImageView;

import java.util.List;

/**
 * Created by xian on 2018/2/10.
 */
@CreatePresenter(SongListPresenter.class)
public class SongListFragment extends BaseMvpFragment<SongListContract.View, SongListPresenter> implements SongListContract.View {

    public static Fragment newInstance(String title) {
        SongListFragment fragment = new SongListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_song_list;
    }

    private CoordinatorLayout mCoordinatorLayout;
    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private OuterLayerImageView mAlbumCover;
    private SongListAdapter mAdapter;
    private String title;
    private DbManager mDbManager;

    @Override
    protected void init() {
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mAlbumCover = (OuterLayerImageView) findViewById(R.id.album_cover);

        mDbManager = new DbManager(getActivity());

        title = getArguments().getString("title");

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
        ((MainActivity) getActivity()).setSupportActionBar(mToolbar);
        ActionBar supportActionBar = ((MainActivity) getActivity()).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        //设置还没收缩时状态下字体颜色
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        //设置收缩后Toolbar上字体的颜色
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        SystemBarHelper.setHeightAndPadding(getActivity(), mToolbar);
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new SongListAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        MusicManager.get().addStateObservable(mAdapter);
        mAdapter.setOnItemClickListener((musicInfo, position) -> {
            mDbManager.AsySavePlayList(mAdapter.getDataList())
                    .subscribe(aBoolean -> {
                        if (!MusicManager.isCurrMusicIsPlayingMusic(mAdapter.getDataList().get(position))) {
                            MusicManager.get().playMusic(mAdapter.getDataList(), position, true);
                        }
                    }, throwable -> {
                        Toast.makeText(mContext, "播放失败", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void initFloatingActionButton() {
        mFloatingActionButton.setClickable(true);
        mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.colorPrimary)));
        mFloatingActionButton.setTranslationY(-DisplayUtil.dip2px(getActivity(), 32));
        mFloatingActionButton.setOnClickListener(v -> {
            mDbManager.AsySavePlayList(mAdapter.getDataList())
                    .subscribe(aBoolean -> {
                        if (!MusicManager.isCurrMusicIsPlayingMusic(mAdapter.getDataList().get(0))) {
                            MusicManager.get().playMusic(mAdapter.getDataList(), 0);
                        }
                    }, throwable -> {
                        Toast.makeText(mContext, "播放失败", Toast.LENGTH_SHORT).show();
                    });
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
        GlideUtil.loadImageByUrl(getActivity(), getPresenter().getAlbumCover(title), mAlbumCover);
    }

    @Override
    public void onGetSongListSuccess(List<MusicInfo> list) {
        mAdapter.setDataList(list);
        mAdapter.setShowLoadMore(list.size() >= getPresenter().size);
    }

    @Override
    public void loadMoreSongListSuccess(List<MusicInfo> list) {
        mAdapter.addDataList(list);
        mAdapter.setShowLoadMore(list.size() >= getPresenter().size);
    }

    @Override
    public void loadFinishAllData() {
        mAdapter.setShowLoadMore(false);
        mAdapter.showLoadAllDataUI();
        Toast.makeText(mContext, "没有更多了", Toast.LENGTH_SHORT).show();
    }
}
