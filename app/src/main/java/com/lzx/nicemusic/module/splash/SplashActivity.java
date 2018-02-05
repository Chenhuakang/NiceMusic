package com.lzx.nicemusic.module.splash;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpActivity;
import com.lzx.nicemusic.base.mvp.factory.CreatePresenter;
import com.lzx.nicemusic.module.main.MainActivity;
import com.lzx.nicemusic.module.splash.presenter.SplashContract;
import com.lzx.nicemusic.module.splash.presenter.SplashPresenter;
import com.lzx.nicemusic.utils.SystemBarHelper;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by xian on 2018/1/13.
 */
@CreatePresenter(SplashPresenter.class)
public class SplashActivity extends BaseMvpActivity<SplashContract.View, SplashPresenter> implements SplashContract.View {

    private ImageView mImageSplash;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        SystemBarHelper.hideStatusBar(getWindow(), true);
        mImageSplash = findViewById(R.id.image_splash);
     //   getPresenter().requestMusicList();
        finishTask();
    }

    private void finishTask() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void requestMainDataSuccess(boolean hasCache) {
        if (!hasCache) {
            finishTask();
        } else {
            Observable.timer(2000, TimeUnit.MILLISECONDS)
                    .compose(bindToLifecycle())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> finishTask());
        }
    }
}
