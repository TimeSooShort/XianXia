package com.miao.android.xianxia.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miao.android.xianxia.bean.CommentBean;

import java.util.List;

/**
 * Created by Administrator on 2016/10/14.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    private final Context mContext;
    private List<CommentBean> mBeanList;
    private LayoutInflater mInflater;

    public CommentsAdapter(Context context, List<CommentBean> beanList) {
        mContext = context;
        mBeanList = beanList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(CommentsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class CommentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public CommentsViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
