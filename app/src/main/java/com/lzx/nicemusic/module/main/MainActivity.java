package com.lzx.nicemusic.module.main;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.model.GlideUrl;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpActivity;
import com.lzx.nicemusic.base.mvp.factory.CreatePresenter;
import com.lzx.nicemusic.bean.BannerInfo;
import com.lzx.nicemusic.bean.HomeInfo;
import com.lzx.nicemusic.bean.MainDataList;
import com.lzx.nicemusic.module.main.adapter.MainAdapter;
import com.lzx.nicemusic.module.main.presenter.MainContract;
import com.lzx.nicemusic.module.main.presenter.MainPresenter;
import com.lzx.nicemusic.module.main.sectioned.BannerSection;
import com.lzx.nicemusic.module.main.sectioned.NewMusicSection;
import com.lzx.nicemusic.utils.DisplayUtil;
import com.lzx.nicemusic.utils.GlideUtil;
import com.lzx.nicemusic.utils.LogUtil;
import com.lzx.nicemusic.widget.OuterLayerImageView;
import com.lzx.nicemusic.widget.banner.BannerView;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

@CreatePresenter(MainPresenter.class)
public class MainActivity extends BaseMvpActivity<MainContract.View, MainPresenter> implements MainContract.View {

    private RecyclerView mRecyclerView;
    private TextView mEdSearch;
    private View mBgSearch;
    private MainAdapter mMainAdapter;

    private float mDistanceY = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mEdSearch = findViewById(R.id.ed_search);
        mBgSearch = findViewById(R.id.bg_search);
        mRecyclerView = findViewById(R.id.recycle_view);
        mMainAdapter = new MainAdapter(this);
        GridLayoutManager glm = new GridLayoutManager(this, 12);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mMainAdapter.getItemViewType(position)) {
                    case HomeInfo.TYPE_ITEM_BANNER:
                    case HomeInfo.TYPE_ITEM_TITLE:
                    case HomeInfo.TYPE_ITEM_ONE:
                    case HomeInfo.TYPE_ITEM_LONGLEGS:
                    case HomeInfo.TYPE_ITEM_ARTS:
                        return 12;
                    case HomeInfo.TYPE_ITEM_TWO:
                        return 6;
                    case HomeInfo.TYPE_ITEM_THREE:
                        return 4;
                    default:
                        return 12;
                }
            }
        });
        mRecyclerView.setLayoutManager(glm);
        mRecyclerView.setAdapter(mMainAdapter);
        int bgSearchHeight = DisplayUtil.dip2px(this, 80);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mDistanceY += dy;
                if (mDistanceY <= bgSearchHeight) {
                    if (mDistanceY < 0) {
                        mDistanceY = 0;
                    }
                    float scale = mDistanceY / bgSearchHeight;
                    mBgSearch.setAlpha(scale);
                } else {
                    mDistanceY = bgSearchHeight;
                    mBgSearch.setAlpha(1f);
                }
            }
        });
        getPresenter().requestMusicList();
    }

    @Override
    public void requestMainDataSuccess(List<HomeInfo> dataList) {
        mMainAdapter.setHomeInfos(dataList);
    }
}

