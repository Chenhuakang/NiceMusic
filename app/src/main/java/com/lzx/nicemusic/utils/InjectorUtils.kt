package com.lzx.nicemusic.utils

import android.content.Context
import com.lzx.nicemusic.activities.model.MainViewModel

object InjectorUtils {
    fun provideMainActivityViewModel(context: Context): MainViewModel.Factory =
            MainViewModel.Factory(context)
}