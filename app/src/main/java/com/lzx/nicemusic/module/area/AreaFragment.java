package com.lzx.nicemusic.module.area;

import android.support.v4.app.Fragment;

import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpFragment;

/**
 * Created by xian on 2018/1/14.
 */

public class AreaFragment extends BaseMvpFragment {

    public static Fragment getInstance(){
        Fragment fragment = new AreaFragment();
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_area;
    }

    @Override
    protected void init() {

    }
}
