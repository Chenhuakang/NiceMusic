package com.lzx.nicemusic.module.area;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpActivity;
import com.lzx.nicemusic.module.area.adapter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.hugeterry.coordinatortablayout.CoordinatorTabLayout;

/**
 * 地区
 * Created by xian on 2018/1/14.
 */

public class AreaActivity extends BaseMvpActivity {

    private CoordinatorTabLayout mCoordinatorTabLayout;
    private ViewPager mViewPager;
    private List<Fragment> mFragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_area;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mCoordinatorTabLayout = findViewById(R.id.coordinatortablayout);
        mViewPager = findViewById(R.id.view_pager);
        initFragments();
        initViewPager();
        //头部的图片数组
        int[] mImageArray = new int[]{
                R.drawable.image_korea,
                R.drawable.image_mainland,
                R.drawable.image_hongkong,
                R.drawable.image_occident,
                R.drawable.image_japan
        };
        int[] mColorArray = new int[]{
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light};
        mCoordinatorTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mCoordinatorTabLayout.setImageArray(mImageArray, mColorArray);
        mCoordinatorTabLayout.setTitle("地区")
                .setImageArray(mImageArray)
                .setupWithViewPager(mViewPager);
    }

    private void initFragments() {
        titles.add("欧美");
        titles.add("内地");
        titles.add("港台");
        titles.add("韩国");
        titles.add("日本");
        for (String title : titles) {
            mFragments.add(AreaFragment.getInstance());
        }
    }

    private void initViewPager() {
        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), mFragments, titles));
    }
}
