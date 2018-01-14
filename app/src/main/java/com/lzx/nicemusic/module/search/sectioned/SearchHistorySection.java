package com.lzx.nicemusic.module.search.sectioned;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzx.nicemusic.R;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by xian on 2018/1/14.
 */

public class SearchHistorySection extends StatelessSection {

    private Context mContext;
    private List<String> hotSearchs = new ArrayList<>();
    private List<String> historys = new ArrayList<>();
    private View.OnClickListener mOnClickListener;

    public SearchHistorySection(Context context, List<String> hotSearchs,
                                List<String> historys, View.OnClickListener onClickListener) {
        super(new SectionParameters.Builder(R.layout.section_search_item)
                .headerResourceId(R.layout.section_search_header).build());
        mContext = context;
        this.hotSearchs = hotSearchs;
        this.historys = historys;
        mOnClickListener = onClickListener;
    }

    @Override
    public int getContentItemsTotal() {
        return historys.size();
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder viewHolder) {
        HeaderHolder holder = (HeaderHolder) viewHolder;
        holder.mFlowLayout.setAdapter(new TagAdapter<String>(hotSearchs) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_tag, holder.mFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new HistoryHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        HistoryHolder holder = (HistoryHolder) viewHolder;
        holder.mSearchTitle.setText(hotSearchs.get(position));
        holder.mBtnDelete.setOnClickListener(mOnClickListener);
        holder.mSearchTitle.setOnClickListener(mOnClickListener);
    }

    class HeaderHolder extends RecyclerView.ViewHolder {
        TagFlowLayout mFlowLayout;

        public HeaderHolder(View itemView) {
            super(itemView);
            mFlowLayout = itemView.findViewById(R.id.flow_layout);
        }
    }

    class HistoryHolder extends RecyclerView.ViewHolder {
        ImageView mBtnDelete;
        TextView mSearchTitle;

        public HistoryHolder(View itemView) {
            super(itemView);
            mBtnDelete = itemView.findViewById(R.id.btn_delete);
            mSearchTitle = itemView.findViewById(R.id.search_title);
        }
    }
}
