package com.lzx.nicemusic.module;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.model.GlideUrl;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpActivity;
import com.lzx.nicemusic.base.mvp.factory.CreatePresenter;
import com.lzx.nicemusic.bean.BannerInfo;
import com.lzx.nicemusic.module.main.presenter.MainContract;
import com.lzx.nicemusic.module.main.presenter.MainPresenter;
import com.lzx.nicemusic.utils.GlideUtil;
import com.lzx.nicemusic.widget.OuterLayerImageView;
import com.lzx.nicemusic.widget.banner.BannerView;

import java.util.List;

@CreatePresenter(MainPresenter.class)
public class MainActivity extends BaseMvpActivity<MainContract.View, MainPresenter> implements MainContract.View {

    private BannerView mBannerView;
    private TextView mEdSearch;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mBannerView = findViewById(R.id.banner_view);
        mEdSearch = findViewById(R.id.ed_search);

        getPresenter().requestBanner();
    }

    @Override
    public void requestBannerSuccess(List<BannerInfo> list) {
        mBannerView
                .setPointsGravity(Gravity.RIGHT)
                .setPointsMargin(5, 0, 5, 10)
                .delayTime(8)
                .build(list, new BannerView.ViewHolderCreator<BannerInfo>() {

                    @Override
                    public View createHolderView(BannerInfo bannerInfo) {
                        View bannerView = View.inflate(MainActivity.this, R.layout.item_banner_view, null);
                        OuterLayerImageView mImageBanner = bannerView.findViewById(R.id.item_banner);
                        GlideUtil.loadImageByUrl(MainActivity.this, bannerInfo.getThumb(), mImageBanner);
                        return bannerView;
                    }
                });
    }
}

