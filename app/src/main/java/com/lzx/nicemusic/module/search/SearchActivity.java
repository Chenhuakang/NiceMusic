package com.lzx.nicemusic.module.search;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageView;

import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpActivity;

/**
 * Created by xian on 2018/1/14.
 */

public class SearchActivity extends BaseMvpActivity {

    private ImageView mBtnBack;
    private EditText mEdSearch;
    private RecyclerView mRecyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mBtnBack = findViewById(R.id.btn_back);
        mEdSearch = findViewById(R.id.ed_search);
        mRecyclerView = findViewById(R.id.recycle_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}
