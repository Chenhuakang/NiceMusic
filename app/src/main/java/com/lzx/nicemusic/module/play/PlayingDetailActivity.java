package com.lzx.nicemusic.module.play;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpActivity;
import com.lzx.nicemusic.base.BaseMvpFragment;
import com.lzx.nicemusic.lib.bean.MusicInfo;
import com.lzx.nicemusic.utils.GlideUtil;

/**
 * Created by xian on 2018/1/21.
 */

public class PlayingDetailActivity extends BaseMvpActivity {

    private TextView mMusicName;
    private ImageView mMusicCover, mBlurImageBg;

    private MusicInfo mMusicInfo;

    public static void launch(Context context, MusicInfo info) {
        Intent intent = new Intent(context, PlayingDetailActivity.class);
        intent.putExtra("MusicInfo", info);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_playing_detail;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

        mMusicInfo = getIntent().getParcelableExtra("MusicInfo");

        mMusicName = findViewById(R.id.music_name);
        mMusicCover = findViewById(R.id.music_cover);
        mBlurImageBg = findViewById(R.id.blur_image_bg);

        mMusicName.setText(mMusicInfo.musicTitle);
        GlideUtil.loadImageByUrl(this, mMusicInfo.musicCover, mMusicCover);
        GlideUtil.loadBlurImage(this, "http://qukufile2.qianqian.com/data2/pic/10e06807a15c04ba2394f8e428975346/566221280/566221280.jpg@s_1,w_1000,h_1000", mBlurImageBg);
    }
}
