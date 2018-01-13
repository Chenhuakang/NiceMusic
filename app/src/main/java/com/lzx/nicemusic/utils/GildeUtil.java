package com.lzx.nicemusic.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by xian on 2018/1/13.
 */

public class GildeUtil {

    public static void loadImageByUrl(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }

}
