package com.lzx.nicemusic.floatwindow;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

public class WindowCreater {
    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;
    private Context mContext;

    public WindowCreater(Context context) {
        mContext = context;
        createWindow();
    }

    private void createWindow() {
        //创建悬浮窗
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams(
                dip2px(mContext, 150),
                dip2px(mContext, 50),
                30,
                180,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT
        );
        mLayoutParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
    }

    public void addViewLayout(View view) {
        mWindowManager.addView(view, mLayoutParams);
    }

    public void updateViewLayout(View view) {
        mWindowManager.updateViewLayout(view, mLayoutParams);
    }

    public void updateViewLayoutLocation(View view, float x, float y) {
        mLayoutParams.x += x;
        mLayoutParams.y -= y;
        mWindowManager.updateViewLayout(view, mLayoutParams);
    }

    public void removeFloatView(View view) {
        mWindowManager.removeView(view);
    }

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
