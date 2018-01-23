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
import com.lzx.musiclibrary.bean.MusicInfo;
import com.lzx.nicemusic.module.search.presenter.SearchContract;
import com.lzx.nicemusic.module.search.presenter.SearchPresenter;
import com.lzx.nicemusic.module.search.sectioned.SearchHistorySection;
import com.lzx.nicemusic.module.search.sectioned.SearchResultSection;
import com.lzx.musiclibrary.utils.LogUtil;

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
    private int deletePosition = -1;

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
                            getPresenter().requestDefaultSearchData();
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
            case R.id.btn_delete:
                deletePosition = (int) view.getTag(R.id.key_search_position);
                getPresenter().deleteHistory(String.valueOf(view.getTag(R.id.key_search_title)));
                break;
            case R.id.search_title:
                String tag = (String) view.getTag();
                mEdSearch.setText(tag);
                mEdSearch.setSelection(tag.length());
                getPresenter().searchMusic(tag);
                break;
        }
    }

    @Override
    public void loadDefaultSearchDataSuccess(List<String> hotSearch, List<String> historys) {
        mSearchHistorySection = new SearchHistorySection(this, hotSearch, historys);
        mSearchHistorySection.setOnTagClickListener((view, position, parent) -> {
            String tag = hotSearch.get(position);
            mEdSearch.setText(tag);
            mEdSearch.setSelection(tag.length());
            getPresenter().searchMusic(tag);
            getPresenter().addHistory(tag);
            return true;
        });
        mSearchHistorySection.setOnClickListener(this);
        mSectionedRecyclerViewAdapter.addSection(SearchHistorySectionTag, mSearchHistorySection);
        mSectionedRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void deleteHistorySuccess() {
        if (deletePosition != -1) {
            mSectionedRecyclerViewAdapter.notifyItemRemovedFromSection(SearchHistorySectionTag, deletePosition);
            mSearchHistorySection.removeHistory(deletePosition);
        }
    }

    @Override
    public void searchSuccess(List<MusicInfo> infoList) {
        mSectionedRecyclerViewAdapter.removeSection(SearchHistorySectionTag);
        mSearchResultSection = new SearchResultSection(this, infoList, getPresenter());
        mSectionedRecyclerViewAdapter.addSection(SearchResultSectionTag, mSearchResultSection);
        mSectionedRecyclerViewAdapter.notifyDataSetChanged();
    }
}
