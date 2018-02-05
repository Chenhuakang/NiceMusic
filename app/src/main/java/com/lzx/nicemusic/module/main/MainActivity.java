package com.lzx.nicemusic.module.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lzx.musiclibrary.aidl.model.MusicInfo;
import com.lzx.musiclibrary.utils.LogUtil;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpActivity;
import com.lzx.nicemusic.base.mvp.factory.CreatePresenter;
import com.lzx.nicemusic.module.area.AreaActivity;
import com.lzx.nicemusic.module.main.presenter.MainContract;
import com.lzx.nicemusic.module.main.presenter.MainPresenter;
import com.lzx.nicemusic.module.main.sectioned.HomeItemSectioned;
import com.lzx.nicemusic.module.main.sectioned.MainItemSectioned;
import com.lzx.nicemusic.module.search.SearchActivity;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

@CreatePresenter(MainPresenter.class)
public class MainActivity extends BaseMvpActivity<MainContract.View, MainPresenter> implements MainContract.View, View.OnClickListener {

    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private MaterialSearchView mSearchView;
    private SectionedRecyclerViewAdapter mSectionedAdapter;
    private Intent intent;
    private float mDistanceY = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
//        mAppBarLayout = findViewById(R.id.app_bar);
//        mToolbar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.recycle_view);
//        mSearchView = findViewById(R.id.search_view);
//
//        mToolbar.setTitle("");
//        setSupportActionBar(mToolbar);
//
//        initSearchView();
//
        mSectionedAdapter = new SectionedRecyclerViewAdapter();
//        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
//        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                switch (mSectionedAdapter.getSectionItemViewType(position)) {
//                    case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
//                        return 2;
//                    case SectionedRecyclerViewAdapter.VIEW_TYPE_FOOTER:
//                        return 2;
//                    default:
//                        return 1;
//                }
//            }
//        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mSectionedAdapter);
        mSectionedAdapter.addSection(new MainItemSectioned(this));
//        getPresenter().requestMusicList();


    }


    private void initSearchView() {
        //初始化SearchBar
        mSearchView.setVoiceSearch(false);
        mSearchView.setCursorDrawable(R.drawable.custom_cursor);
        mSearchView.setEllipsize(true);
        mSearchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                LogUtil.i("onQueryTextSubmit = " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                LogUtil.i("onQueryTextChange = " + newText);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.id_action_search);
        mSearchView.setMenuItem(item);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    LogUtil.i("onActivityResult = " + searchWrd);
                    mSearchView.setQuery(searchWrd, false);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
//        if (mSearchView.isSearchOpen()) {
//            mSearchView.closeSearch();
//        } else {
//            super.onBackPressed();
//        }
    }

    @Override
    public void requestMainDataSuccess(ConcurrentMap<String, List<MusicInfo>> map, List<Map.Entry<String, String>> types) {
        for (Map.Entry<String, String> mapping : types) {
            if (map.containsKey(mapping.getKey())) {
                mSectionedAdapter.addSection(new HomeItemSectioned(
                        this, map.get(mapping.getKey()), mapping.getValue()));
            }
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

