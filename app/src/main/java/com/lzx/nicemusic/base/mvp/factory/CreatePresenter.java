package com.lzx.nicemusic.base.mvp.factory;

import com.lzx.nicemusic.base.mvp.BaseContract;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * 创建Presenter的注解
 * @author lzx
 * @date 2017/12/7
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface CreatePresenter {
    Class<? extends BaseContract.BasePresenter> value();
}
