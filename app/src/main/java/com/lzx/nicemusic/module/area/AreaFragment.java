package com.lzx.nicemusic.module.area;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lzx.musiclibrary.aidl.model.MusicInfo;
import com.lzx.musiclibrary.utils.LogUtil;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpFragment;
import com.lzx.nicemusic.base.mvp.factory.CreatePresenter;
import com.lzx.nicemusic.module.area.presenter.AreaContract;
import com.lzx.nicemusic.module.area.presenter.AreaPresenter;
import com.lzx.nicemusic.module.area.sectioned.AreaSection;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * Created by xian on 2018/1/14.
 */
@CreatePresenter(AreaPresenter.class)
public class AreaFragment extends BaseMvpFragment<AreaContract.View, AreaPresenter>
        implements AreaContract.View, AreaSection.OnItemClickListener {

    private String topic;

    public static Fragment getInstance(String topic) {
        Fragment fragment = new AreaFragment();
        Bundle bundle = new Bundle();
        bundle.putString("topic", topic);
        fragment.setArguments(bundle);
        return fragment;
    }

    private SectionedRecyclerViewAdapter mSectionedRecyclerViewAdapter;
    private RecyclerView mRecyclerView;
    private AreaSection mAreaSection;


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            LogUtil.i("服务链接成功...");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.fragment_area;
    }

    @Override
    protected void init() {
        topic = getArguments().getString("topic");

        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
        mRecyclerView.setAdapter(mSectionedRecyclerViewAdapter);



        getPresenter().requestAreaData(topic);
    }

    @Override
    public void onItemClick(List<MusicInfo> list, MusicInfo musicInfo, int position) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unbindService(mServiceConnection);
    }

    @Override
    protected void lazyLoadData() {

    }

    @Override
    public void loadAreaDataSuccess(List<MusicInfo> infoList) {

        mAreaSection = new AreaSection(getActivity(), infoList);
        mAreaSection.setOnItemClickListener(this);
        mSectionedRecyclerViewAdapter.addSection(mAreaSection);
        mSectionedRecyclerViewAdapter.notifyDataSetChanged();
    }


}
