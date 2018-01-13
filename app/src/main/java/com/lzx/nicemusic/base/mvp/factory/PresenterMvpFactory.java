package com.lzx.nicemusic.base.mvp.factory;


import com.lzx.nicemusic.base.mvp.BaseContract;

/**
 *  Presenter工厂接口
 * @author lzx
 * @date 2017/12/7
 */

public interface PresenterMvpFactory<V extends BaseContract.BaseView, P extends BaseContract.BasePresenter<V>> {
    P createPresenter();
}
