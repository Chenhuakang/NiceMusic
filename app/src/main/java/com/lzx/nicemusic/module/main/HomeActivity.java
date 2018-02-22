package com.lzx.nicemusic.module.main;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpActivity;
import com.lzx.nicemusic.base.mvp.factory.CreatePresenter;
import com.lzx.nicemusic.module.main.sectioned.BannerSectioned;
import com.lzx.nicemusic.module.main.sectioned.ChartTopSectioned;
import com.lzx.nicemusic.module.main.sectioned.HomeSongSection;
import com.lzx.nicemusic.module.main.sectioned.TitleSectioned;
import com.lzx.nicemusic.module.songlist.presenter.SongListContract;
import com.lzx.nicemusic.module.songlist.presenter.SongListPresenter;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * @author lzx
 * @date 2018/2/14
 */
@CreatePresenter(SongListPresenter.class)
public class HomeActivity extends BaseMvpActivity<SongListContract.View, SongListPresenter> implements SongListContract.View {

    private RecyclerView mRecyclerView;
    private SectionedRecyclerViewAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        mRecyclerView = findViewById(R.id.recycle_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SectionedRecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);
        getPresenter().requestSongList("热歌榜");
    }

    @Override
    public void onGetSongListSuccess(List<SongInfo> list, String title) {
        if (title.equals("热歌榜")) {
            mAdapter.addSection(new BannerSectioned(this));
            mAdapter.addSection(new TitleSectioned(this, "Chart Top 10"));
            mAdapter.addSection(new ChartTopSectioned(this, list));
            mAdapter.addSection(new TitleSectioned(this, "New Songs"));
            getPresenter().requestSongList("新歌榜");
        } else if (title.equals("新歌榜")) {
            mAdapter.addSection(new HomeSongSection(this, list));
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadMoreSongListSuccess(List<SongInfo> list, String title) {

    }

    @Override
    public void loadFinishAllData() {

    }
}
