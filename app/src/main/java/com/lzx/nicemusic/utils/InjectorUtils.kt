package com.lzx.nicemusic.utils

import android.content.Context
import com.lzx.nicemusic.activities.model.MainViewModel

object InjectorUtils {
    fun provideMainActivityViewModel(): MainViewModel.Factory =
            MainViewModel.Factory()
}