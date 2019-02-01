package com.lzx.nicemusic.utils;

import android.content.Context;

import com.lzx.nicemusic.activities.model.MainViewModel;


public class InjectorUtils {

    public static MainViewModel.Factory provideMainActivityViewModel(Context context) {
        return new MainViewModel.Factory(context);
    }
}
