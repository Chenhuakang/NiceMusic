package com.lzx.nicemusic.module.main;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lzx.nicemusic.MainActivity;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpFragment;
import com.lzx.nicemusic.module.main.sectioned.MainItemSectioned;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * Created by xian on 2018/2/10.
 */

public class MainFragment extends BaseMvpFragment {

    public static Fragment newInstance() {
        return new MainFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_main;
    }

    private RecyclerView mRecyclerView;
    private SectionedRecyclerViewAdapter mSectionedAdapter;
    private MainItemSectioned mItemSectioned;

    @Override
    protected void init() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        mSectionedAdapter = new SectionedRecyclerViewAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mSectionedAdapter);
        mItemSectioned = new MainItemSectioned(getActivity());
        mSectionedAdapter.addSection(mItemSectioned);
        mItemSectioned.setOnItemClickListener(title -> {
            ((MainActivity) getActivity()).switchFragment(title);
        });
    }
}
