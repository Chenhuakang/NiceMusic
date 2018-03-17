package com.lzx.nicemusic.base;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lzx.musiclibrary.helper.ResourceHelper;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.mvp.BaseContract;
import com.lzx.nicemusic.base.mvp.BaseMvpProxy;
import com.lzx.nicemusic.base.mvp.factory.PresenterMvpFactory;
import com.lzx.nicemusic.base.mvp.factory.PresenterMvpFactoryImpl;
import com.lzx.nicemusic.base.mvp.factory.PresenterProxyInterface;
import com.lzx.nicemusic.utils.SystemBarHelper;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;


/**
 * 基础activity
 *
 * @author lzx
 * @date 2017/12/5
 */
public abstract class BaseMvpActivity<V extends BaseContract.BaseView, P extends BaseContract.BasePresenter<V>> extends RxAppCompatActivity implements PresenterProxyInterface<V, P>, BaseContract.BaseView {
    protected Context mContext;
    private static final String PRESENTER_SAVE_KEY = "presenter_save_key";

    /**
     * 创建被代理对象,传入默认Presenter的工厂
     */
    private BaseMvpProxy<V, P> mProxy;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());
        mContext = this;
        mProxy = new BaseMvpProxy<>(PresenterMvpFactoryImpl.<V, P>createFactory(getClass()), this);
        if (savedInstanceState != null) {
            mProxy.onRestoreInstanceState(savedInstanceState.getBundle(PRESENTER_SAVE_KEY));
        }
        mProxy.onResume((V) this);
        initStatusBar();
        init(savedInstanceState);
    }

    /**
     * 布局文件
     *
     * @return 布局文件
     */
    protected abstract @LayoutRes
    int getLayoutId();

    /**
     * 初始化StatusBar
     */
    protected void initStatusBar() {
        mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {
            SystemBarHelper.immersiveStatusBar(this, 0);
            SystemBarHelper.setHeightAndPadding(this, mToolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("");
            mToolbar.setTitleMarginStart(0);
            mToolbar.setContentInsetStartWithNavigation(0);
            mToolbar.setNavigationOnClickListener(v -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    finish();
                } else {
                    supportFinishAfterTransition();
                }
            });
        }
    }

    /**
     * 初始化
     *
     * @param savedInstanceState
     */
    protected abstract void init(Bundle savedInstanceState);

    /**
     * 请求完成
     */
    @Override
    public void complete() {

    }

    /**
     * 是否显示进度条
     *
     * @param isShow
     */
    @Override
    public void showProgressUI(boolean isShow) {

    }

    /**
     * 请求失败
     *
     * @param msg        错误信息
     * @param isLoadMore 是否加载更多，用来区分刷新出错还是加载更多出错
     */
    @Override
    public void showError(String msg, boolean isLoadMore) {

    }

    @Override
    public void setPresenterFactory(PresenterMvpFactory<V, P> presenterFactory) {
        mProxy.setPresenterFactory(presenterFactory);
    }

    @Override
    public PresenterMvpFactory<V, P> getPresenterFactory() {
        return mProxy.getPresenterFactory();
    }

    @Override
    public P getPresenter() {
        return mProxy.getPresenter();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(PRESENTER_SAVE_KEY, mProxy.onSaveInstanceState());
    }


    /**
     * 销毁
     */
    @Override
    protected void onDestroy() {
        mProxy.onDestroy();
        super.onDestroy();
    }


    /**
     * 隐藏View
     *
     * @param views 视图
     */
    protected void gone(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 显示View
     *
     * @param views 视图
     */
    protected void visible(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    protected void invisible(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * 隐藏View
     *
     * @param id
     */
    protected void gone(final @IdRes int... id) {
        if (id != null && id.length > 0) {
            for (int resId : id) {
                View view = $(resId);
                if (view != null)
                    gone(view);
            }
        }
    }

    protected void invisible(final @IdRes int... id) {
        if (id != null && id.length > 0) {
            for (int resId : id) {
                View view = $(resId);
                if (view != null)
                    invisible(view);
            }
        }
    }

    /**
     * 显示View
     *
     * @param id
     */
    protected void visible(final @IdRes int... id) {
        if (id != null && id.length > 0) {
            for (int resId : id) {
                View view = $(resId);
                if (view != null)
                    visible(view);
            }
        }
    }

    /**
     * findViewById
     *
     * @param id
     * @return
     */
    protected View $(@IdRes int id) {
        return this.findViewById(id);
    }


}
