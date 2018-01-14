package com.lzx.nicemusic.utils;

import android.text.TextUtils;

/**
 * Created by xian on 2018/1/14.
 */

public class FormatUtil {


    public static String formatNum(String num) {
        if (TextUtils.isEmpty(num)) {
            num = "0";
        }
        long number = Long.parseLong(num);
        long result = 0;
        if (number > 10000) {
            result = number / 10000;
            return String.valueOf(result) + "万";
        } else if (number > 1000) {
            result = number / 1000;
            return String.valueOf(result) + "千";
        } else {
            return String.valueOf(number);
        }
    }

}
