package com.lzx.nicemusic.floatwindow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lzx.musiclibrary.aidl.listener.OnPlayerEventListener;
import com.lzx.musiclibrary.aidl.model.SongInfo;
import com.lzx.musiclibrary.manager.MusicManager;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.utils.GlideUtil;
import com.lzx.nicemusic.widget.CircleImageView;

public class FloatWindowUtils implements View.OnClickListener, OnPlayerEventListener, View.OnTouchListener {
    private WindowCreater mWindowCreater;
    private static Context mContext;
    private View mView;
    private ImageView mBtnClose;
    private CircleImageView mSongCover;
    private boolean isShowWindow = false;
    private float mStartX, mStartY;
    private long startTime = 0;
    private long endTime = 0;

    public static void init(Context context) {
        mContext = context;
        new FloatWindowUtils();
    }

    private FloatWindowUtils() {
        mWindowCreater = new WindowCreater(mContext);
        MusicManager.get().addPlayerEventListener(this);
    }

    public static FloatWindowUtils getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final FloatWindowUtils sInstance = new FloatWindowUtils();
    }

    public void showFloatWindow() {
        if (!isShowWindow) {
            mView = View.inflate(mContext, R.layout.layout_float_window, null);
            mBtnClose = mView.findViewById(R.id.btn_close);
            mSongCover = mView.findViewById(R.id.song_cover);
            mBtnClose.setOnClickListener(this);
            SongInfo songInfo = MusicManager.get().getCurrPlayingMusic();
            if (songInfo != null) {
                GlideUtil.loadImageByUrl(mContext, songInfo.getSongCover(), mSongCover);
            } else {
                GlideUtil.loadImageByUrl(mContext, R.color.refresh_pink_background, mSongCover);
            }
            mView.setOnTouchListener(this);
            mWindowCreater.addViewLayout(mView);
            isShowWindow = true;
        }
    }

    public void updateFloatWindow() {
        if (mView != null && isShowWindow) {
            SongInfo songInfo = MusicManager.get().getCurrPlayingMusic();
            if (songInfo != null) {
                GlideUtil.loadImageByUrl(mContext, songInfo.getSongCover(), mSongCover);
            } else {
                GlideUtil.loadImageByUrl(mContext, R.color.refresh_pink_background, mSongCover);
            }
            mWindowCreater.updateViewLayout(mView);
        }
    }

    public void removeFloatWindow() {
        if (mView != null && isShowWindow) {
            mWindowCreater.removeFloatView(mView);
            isShowWindow = false;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 当前值以屏幕左上角为原点
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getRawX();
                mStartY = event.getRawY();
                startTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getRawX() - mStartX;
                float y = event.getRawY() - mStartY;
                if (isShowWindow && mView != null) {
                    mWindowCreater.updateViewLayoutLocation(mView, x, y);
                }
                mStartX = event.getRawX();
                mStartY = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                //当从点击到弹起小于0.1秒的时候,则判断为点击
                endTime = System.currentTimeMillis();
                if ((endTime - startTime) < 0.3 * 1000L) {
                    onViewClick();
                }
                break;
        }
        // 消耗触摸事件
        return true;
    }

    private void onViewClick() {
        SongInfo songInfo = MusicManager.get().getCurrPlayingMusic();
        Toast.makeText(mContext, songInfo != null ? songInfo.getSongName() : "点击", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMusicSwitch(SongInfo music) {
        updateFloatWindow();
    }

    @Override
    public void onPlayerStart() {
        showFloatWindow();
    }

    @Override
    public void onPlayerPause() {

    }

    @Override
    public void onPlayCompletion() {

    }

    @Override
    public void onPlayerStop() {

    }

    @Override
    public void onError(String errorMsg) {

    }

    @Override
    public void onAsyncLoading(boolean isFinishLoading) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                removeFloatWindow();
                break;
        }
    }

}
