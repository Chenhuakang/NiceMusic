package com.lzx.nicemusic.base.mvp.factory;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.lzx.nicemusic.base.mvp.BaseContract;


/**
 * 基础Presenter
 *
 * @author lzx
 * @date 2017/12/5
 */

public class BasePresenter<T extends BaseContract.BaseView> implements BaseContract.BasePresenter<T> {

    protected T mView;
    protected Context mContext;

    @Override
    public void attachView(T view, Context context) {
        this.mView = view;
        this.mContext = context;
    }

    @Override
    public void detachView() {
        this.mView = null;
    }

    @Override
    public void onCreatePresenter(@Nullable Bundle savedState) {

    }

    @Override
    public void onDestroyPresenter() {
        mContext = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }
}
