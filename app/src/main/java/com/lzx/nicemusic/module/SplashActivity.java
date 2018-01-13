package com.lzx.nicemusic.module;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpActivity;
import com.lzx.nicemusic.utils.SystemBarHelper;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by xian on 2018/1/13.
 */

public class SplashActivity extends BaseMvpActivity {

    private ImageView mImageSplash;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        SystemBarHelper.hideStatusBar(getWindow(), true);
        mImageSplash = findViewById(R.id.image_splash);
        Observable.timer(2000, TimeUnit.MILLISECONDS)
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> finishTask());
    }

    private void finishTask() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }
}
