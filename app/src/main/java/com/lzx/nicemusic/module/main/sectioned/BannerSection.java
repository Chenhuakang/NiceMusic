package com.lzx.nicemusic.module.main.sectioned;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;

import com.lzx.nicemusic.R;
import com.lzx.nicemusic.bean.BannerInfo;
import com.lzx.nicemusic.utils.GlideUtil;
import com.lzx.nicemusic.widget.OuterLayerImageView;
import com.lzx.nicemusic.widget.banner.BannerView;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by xian on 2018/1/13.
 */

public class BannerSection extends StatelessSection {

    private Context mContext;
    private List<BannerInfo> mBannerInfos;

    public BannerSection(Context context, List<BannerInfo> bannerInfos) {
        super(new SectionParameters.Builder(R.layout.layout_banner_view).build());
        mContext = context;
        mBannerInfos = bannerInfos;
    }

    @Override
    public int getContentItemsTotal() {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new BannerHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        BannerHolder holder = (BannerHolder) viewHolder;
        holder.mBannerView
                .setPointsGravity(Gravity.RIGHT)
                .setPointsMargin(5, 0, 5, 10)
                .delayTime(8)
                .build(mBannerInfos, new BannerView.ViewHolderCreator<BannerInfo>() {

                    @Override
                    public View createHolderView(BannerInfo bannerInfo) {
                        View bannerView = View.inflate(mContext, R.layout.layout_banner_view, null);
                        OuterLayerImageView mImageBanner = bannerView.findViewById(R.id.item_banner);
                        GlideUtil.loadImageByUrl(mContext, bannerInfo.getThumb(), mImageBanner);
                        return bannerView;
                    }
                });
    }

    private class BannerHolder extends RecyclerView.ViewHolder {

        BannerView mBannerView;
        RelativeLayout mBtnMe, mBtnMusic, mBtnOther;

        BannerHolder(View view) {
            super(view);
            mBannerView = view.findViewById(R.id.banner_view);
            mBtnMe = view.findViewById(R.id.btn_me);
            mBtnMusic = view.findViewById(R.id.btn_music);
            mBtnOther = view.findViewById(R.id.btn_other);
        }
    }
}
