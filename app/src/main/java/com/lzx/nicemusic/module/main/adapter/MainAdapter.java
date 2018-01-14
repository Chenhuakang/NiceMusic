package com.lzx.nicemusic.module.main.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzx.nicemusic.R;
import com.lzx.nicemusic.bean.BannerInfo;
import com.lzx.nicemusic.bean.HomeInfo;
import com.lzx.nicemusic.utils.FormatUtil;
import com.lzx.nicemusic.utils.GlideUtil;
import com.lzx.nicemusic.widget.OuterLayerImageView;
import com.lzx.nicemusic.widget.RectangleImageView;
import com.lzx.nicemusic.widget.SquareImageView;
import com.lzx.nicemusic.widget.banner.BannerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xian on 2018/1/13.
 */

public class MainAdapter extends RecyclerView.Adapter {

    private Context mContext;

    private List<HomeInfo> mHomeInfos = new ArrayList<>();

    public MainAdapter(Context context) {
        mContext = context;
    }

    public void setHomeInfos(List<HomeInfo> homeInfos) {
        mHomeInfos = homeInfos;
    }

    public List<HomeInfo> getHomeInfos() {
        return mHomeInfos;
    }

    @Override
    public int getItemViewType(int position) {
        HomeInfo homeInfo = mHomeInfos.get(position);
        switch (homeInfo.getItemType()) {
            case HomeInfo.TYPE_ITEM_BANNER:
                return HomeInfo.TYPE_ITEM_BANNER;
            case HomeInfo.TYPE_ITEM_TITLE:
                return HomeInfo.TYPE_ITEM_TITLE;
            case HomeInfo.TYPE_ITEM_ONE:
                return HomeInfo.TYPE_ITEM_ONE;
            case HomeInfo.TYPE_ITEM_LONGLEGS:
                return HomeInfo.TYPE_ITEM_LONGLEGS;
            case HomeInfo.TYPE_ITEM_TWO:
                return HomeInfo.TYPE_ITEM_TWO;
            case HomeInfo.TYPE_ITEM_THREE:
                return HomeInfo.TYPE_ITEM_THREE;
            case HomeInfo.TYPE_ITEM_ARTS:
                return HomeInfo.TYPE_ITEM_ARTS;
            default:
                return super.getItemViewType(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case HomeInfo.TYPE_ITEM_BANNER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner_view, parent, false);
                return new BannerHolder(view);
            case HomeInfo.TYPE_ITEM_TITLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_title, parent, false);
                return new TitleHolder(view);
            case HomeInfo.TYPE_ITEM_ONE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type_one, parent, false);
                return new OneHolder(view);
            case HomeInfo.TYPE_ITEM_LONGLEGS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item_longlegs, parent, false);
                return new LongLegsHolder(view);
            case HomeInfo.TYPE_ITEM_TWO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_type_two, parent, false);
                return new TwoHolder(view);
            case HomeInfo.TYPE_ITEM_THREE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item_music, parent, false);
                return new ItemViewHolder(view);
            case HomeInfo.TYPE_ITEM_ARTS:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item_arts, parent, false);
                return new ArtsHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        HomeInfo info = mHomeInfos.get(position);
        if (viewHolder instanceof BannerHolder) {
            BannerHolder holder = (BannerHolder) viewHolder;
            holder.mBannerView
                    .setPointsGravity(Gravity.RIGHT)
                    .setPointsMargin(5, 0, 5, 10)
                    .delayTime(10)
                    .build(info.getBannerList(), new BannerView.ViewHolderCreator<BannerInfo>() {

                        @Override
                        public View createHolderView(BannerInfo bannerInfo) {
                            View bannerView = View.inflate(mContext, R.layout.layout_banner_view, null);
                            OuterLayerImageView mImageBanner = bannerView.findViewById(R.id.item_banner);
                            GlideUtil.loadImageByUrl(mContext, bannerInfo.getThumb(), mImageBanner);
                            return bannerView;
                        }
                    });
        } else if (viewHolder instanceof TitleHolder) {
            TitleHolder holder = (TitleHolder) viewHolder;
            holder.tvTitle.setText(info.getItemTitle());
        } else if (viewHolder instanceof ItemViewHolder) {
            ItemViewHolder holder = (ItemViewHolder) viewHolder;
            holder.mMusicTitle.setText(info.getSongname());
            holder.mHeadsetNum.setText(FormatUtil.formatNum(info.getSongid()));
            GlideUtil.loadImageByUrl(mContext, info.getAlbumpicBig(), holder.mMusicCover);
        } else if (viewHolder instanceof LongLegsHolder) {
            LongLegsHolder holder = (LongLegsHolder) viewHolder;
            holder.mBannerView
                    .isHidePoints(true)
                    .delayTime(10)
                    .build(info.getLongLegs(), new BannerView.ViewHolderCreator<BannerInfo>() {

                        @Override
                        public View createHolderView(BannerInfo bannerInfo) {
                            View bannerView = View.inflate(mContext, R.layout.layout_banner_longlegs, null);
                            OuterLayerImageView mImageBanner = bannerView.findViewById(R.id.item_banner);
                            TextView mBannerTitle = bannerView.findViewById(R.id.banner_title);
                            mBannerTitle.setText(bannerInfo.getTitle());
                            GlideUtil.loadImageByUrl(mContext, bannerInfo.getThumb(), mImageBanner);
                            return bannerView;
                        }
                    });
        } else if (viewHolder instanceof OneHolder) {
            OneHolder holder = (OneHolder) viewHolder;
            holder.mMusicTitle.setText(info.getSongname());
            holder.mReadNum.setText("听过 " + FormatUtil.formatNum(info.getSingerid()));
            GlideUtil.loadImageByUrl(mContext, info.getAlbumpicBig(), holder.mMusicCover);
        } else if (viewHolder instanceof TwoHolder) {
            TwoHolder holder = (TwoHolder) viewHolder;
            holder.mLikeNum.setText(FormatUtil.formatNum(info.getLove()));
            holder.mMusicText.setText(info.getText().trim());
            holder.mMusicName.setText(info.getName());
            GlideUtil.loadImageByUrl(mContext, info.getImage3(), holder.mMusicCover);
        } else if (viewHolder instanceof ArtsHolder) {
            ArtsHolder holder = (ArtsHolder) viewHolder;
            GlideUtil.loadImageByUrl(mContext, info.getArtGirl().getThumb(), holder.mArts);

        }
    }

    @Override
    public int getItemCount() {
        return mHomeInfos.size();
    }

    class BannerHolder extends RecyclerView.ViewHolder {

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

    class TitleHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        TitleHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.header_title);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        SquareImageView mMusicCover;
        TextView mMusicTitle, mHeadsetNum;

        ItemViewHolder(View view) {
            super(view);
            mMusicCover = view.findViewById(R.id.music_cover);
            mMusicTitle = view.findViewById(R.id.music_title);
            mHeadsetNum = view.findViewById(R.id.headset_num);
        }
    }

    class OneHolder extends RecyclerView.ViewHolder {
        OuterLayerImageView mMusicCover;
        TextView mMusicTitle, mReadNum;

        OneHolder(View view) {
            super(view);
            mMusicCover = view.findViewById(R.id.music_cover);
            mMusicTitle = view.findViewById(R.id.music_title);
            mReadNum = view.findViewById(R.id.read_num);
        }
    }

    class LongLegsHolder extends RecyclerView.ViewHolder {

        BannerView mBannerView;

        LongLegsHolder(View view) {
            super(view);

            mBannerView = view.findViewById(R.id.banner_view);
        }
    }

    class TwoHolder extends RecyclerView.ViewHolder {
        RectangleImageView mMusicCover;
        TextView mLikeNum, mMusicText, mMusicName;

        TwoHolder(View view) {
            super(view);
            mMusicCover = view.findViewById(R.id.music_cover);
            mLikeNum = view.findViewById(R.id.like_num);
            mMusicText = view.findViewById(R.id.music_text);
            mMusicName = view.findViewById(R.id.music_name);
        }
    }

    class ArtsHolder extends RecyclerView.ViewHolder {
        ImageView mArts;

        ArtsHolder(View view) {
            super(view);
            mArts = view.findViewById(R.id.music_cover);
        }
    }
}
