package com.lzx.nicemusic.module.search.sectioned;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzx.nicemusic.R;
import com.lzx.nicemusic.module.search.presenter.SearchPresenter;
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
    private TagFlowLayout.OnTagClickListener mOnTagClickListener;
    private View.OnClickListener mOnClickListener;

    public SearchHistorySection(Context context, List<String> hotSearchs,
                                List<String> historys) {
        super(new SectionParameters.Builder(R.layout.section_search_item)
                .headerResourceId(R.layout.section_search_header).build());
        mContext = context;
        this.hotSearchs = hotSearchs;
        this.historys = historys;
    }

    public void removeHistory(int position) {
        historys.remove(position);
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
        holder.mFlowLayout.setOnTagClickListener(mOnTagClickListener);
    }

    public void setOnTagClickListener(TagFlowLayout.OnTagClickListener onTagClickListener) {
        mOnTagClickListener = onTagClickListener;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new HistoryHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        HistoryHolder holder = (HistoryHolder) viewHolder;
        String history = historys.get(position);
        holder.mSearchTitle.setText(history);
        holder.mSearchTitle.setTag(history);
        holder.mBtnDelete.setTag(R.id.key_search_title, history);
        holder.mBtnDelete.setTag(R.id.key_search_position, position);
        holder.mSearchTitle.setOnClickListener(mOnClickListener);
        holder.mBtnDelete.setOnClickListener(mOnClickListener);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
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
