package com.lzx.nicemusic.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpActivity;
import com.lzx.nicemusic.base.mvp.factory.CreatePresenter;
import com.lzx.nicemusic.bean.HomeInfo;
import com.lzx.nicemusic.callback.BaseDiffUtilCallBack;
import com.lzx.nicemusic.helper.DataHelper;
import com.lzx.nicemusic.lib.bean.MusicInfo;
import com.lzx.nicemusic.module.area.AreaActivity;
import com.lzx.nicemusic.module.main.adapter.MainAdapter;
import com.lzx.nicemusic.module.main.presenter.MainContract;
import com.lzx.nicemusic.module.main.presenter.MainPresenter;
import com.lzx.nicemusic.module.main.sectioned.HomeItemSectioned;
import com.lzx.nicemusic.module.search.SearchActivity;
import com.lzx.nicemusic.utils.DisplayUtil;
import com.lzx.nicemusic.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

@CreatePresenter(MainPresenter.class)
public class MainActivity extends BaseMvpActivity<MainContract.View, MainPresenter> implements MainContract.View, View.OnClickListener {

    private RecyclerView mRecyclerView;
  //  private TextView mEdSearch;
  //  private View mBgSearch;
    private SectionedRecyclerViewAdapter mSectionedAdapter;
    private Intent intent;
    private float mDistanceY = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
      //  mEdSearch = findViewById(R.id.ed_search);
      //  mBgSearch = findViewById(R.id.bg_search);
        mRecyclerView = findViewById(R.id.recycle_view);
        mSectionedAdapter = new SectionedRecyclerViewAdapter();
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mSectionedAdapter.getSectionItemViewType(position)) {
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                        return 2;
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_FOOTER:
                        return 2;
                    default:
                        return 1;
                }
            }
        });
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mSectionedAdapter);
        int bgSearchHeight = DisplayUtil.dip2px(this, 80);
//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                mDistanceY += dy;
//                if (mDistanceY <= bgSearchHeight) {
//                    if (mDistanceY < 0) {
//                        mDistanceY = 0;
//                    }
//                    float scale = mDistanceY / bgSearchHeight;
//                    mBgSearch.setAlpha(scale);
//                } else {
//                    mDistanceY = bgSearchHeight;
//                    mBgSearch.setAlpha(1f);
//                }
//            }
//        });
        getPresenter().requestMusicList();

        //搜索
       // mEdSearch.setOnClickListener(this);
    }

    @Override
    public void requestMainDataSuccess(List<MusicInfo> dataList) {
//        BaseDiffUtilCallBack<HomeInfo> callBack = new BaseDiffUtilCallBack<>(mMainAdapter.getHomeInfos(), dataList);
//        callBack.setOnAreItemsTheSameListener((oldData, newData) -> oldData.getFlag().equals(newData.getFlag()));
//        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callBack, true);
//        mMainAdapter.setHomeInfos(dataList);
//        diffResult.dispatchUpdatesTo(mMainAdapter);
//        mMainAdapter.setOnClickListener(this);
        int itemSize = dataList.size() / 10;
        for (int i = 0; i < itemSize; i++) {
            int index = i * 4;
            int size = 4 + index;
            String title = dataList.get(index).musicType;
            mSectionedAdapter.addSection(new HomeItemSectioned(
                    this,
                    DataHelper.subList(dataList, index, size), title));
        }
        mSectionedAdapter.notifyDataSetChanged();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ed_search:
                intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_me:
                break;
            case R.id.btn_music:
                break;
            case R.id.btn_other:
                break;
            case R.id.btn_area:
                intent = new Intent(this, AreaActivity.class);
                startActivity(intent);
                break;
        }
    }
}

