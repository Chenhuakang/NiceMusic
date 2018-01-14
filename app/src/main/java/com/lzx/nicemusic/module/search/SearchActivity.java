package com.lzx.nicemusic.module.search;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpActivity;
import com.lzx.nicemusic.base.mvp.factory.CreatePresenter;
import com.lzx.nicemusic.bean.MusicInfo;
import com.lzx.nicemusic.module.search.presenter.SearchContract;
import com.lzx.nicemusic.module.search.presenter.SearchPresenter;
import com.lzx.nicemusic.module.search.sectioned.SearchHistorySection;
import com.lzx.nicemusic.module.search.sectioned.SearchResultSection;
import com.lzx.nicemusic.utils.LogUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by xian on 2018/1/14.
 */
@CreatePresenter(SearchPresenter.class)
public class SearchActivity extends BaseMvpActivity<SearchContract.View, SearchPresenter> implements SearchContract.View, View.OnClickListener {

    private ImageView mBtnBack;
    private EditText mEdSearch;
    private RecyclerView mRecyclerView;
    private SectionedRecyclerViewAdapter mSectionedRecyclerViewAdapter;
    private SearchHistorySection mSearchHistorySection;
    private SearchResultSection mSearchResultSection;
    private String SearchHistorySectionTag = "SearchHistorySectionTag";
    private String SearchResultSectionTag = "SearchResultSectionTag";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mBtnBack = findViewById(R.id.btn_back);
        mEdSearch = findViewById(R.id.ed_search);
        mRecyclerView = findViewById(R.id.recycle_view);
        mBtnBack.setOnClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
        mRecyclerView.setAdapter(mSectionedRecyclerViewAdapter);

        RxTextView.textChangeEvents(mEdSearch)
                .compose(bindToLifecycle())
                .debounce(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(textViewTextChangeEvent -> {
                    String text = textViewTextChangeEvent.text().toString().trim();
                    if (!TextUtils.isEmpty(text)) {
                        getPresenter().searchMusic(text);
                    } else {
                        if (mSearchResultSection != null) {
                            mSectionedRecyclerViewAdapter.removeSection(SearchResultSectionTag);
                        }
                        if (mSearchHistorySection != null) {
                            mSectionedRecyclerViewAdapter.addSection(SearchHistorySectionTag, mSearchHistorySection);
                        }
                        mSectionedRecyclerViewAdapter.notifyDataSetChanged();
                    }
                }, throwable -> {
                    LogUtil.i("search_error->" + throwable.getMessage());
                });

        getPresenter().requestDefaultSearchData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }

    @Override
    public void loadDefaultSearchDataSuccess(List<String> hotSearch, List<String> historys) {
        mSearchHistorySection = new SearchHistorySection(this, hotSearch, historys, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mSectionedRecyclerViewAdapter.addSection(SearchHistorySectionTag, mSearchHistorySection);
        mSectionedRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void searchSuccess(List<MusicInfo> infoList) {
        mSectionedRecyclerViewAdapter.removeSection(SearchHistorySectionTag);
        mSearchResultSection = new SearchResultSection(this, infoList);
        mSectionedRecyclerViewAdapter.addSection(SearchResultSectionTag, mSearchResultSection);
        mSectionedRecyclerViewAdapter.notifyDataSetChanged();
    }
}
