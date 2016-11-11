package com.miao.android.xianxia.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.miao.android.xianxia.R;
import com.miao.android.xianxia.bean.StoriesBean;
import com.miao.android.xianxia.interfaces.OnRecyclerOnClickListener;

import java.util.List;

/**
 * Created by Administrator on 2016/10/12.
 */

public class StoriesRecyclerAdapter extends RecyclerView.Adapter<StoriesRecyclerAdapter.
        StoriesViewHolder> {

    private final Context mContext;
    private List<StoriesBean> mBeanList;
    private final LayoutInflater mInflater;
    private OnRecyclerOnClickListener mListener;

    public StoriesRecyclerAdapter(Context context, List<StoriesBean> beanList) {
        mContext = context;
        mBeanList = beanList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public StoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.fragment_storyitem, parent, false);
        return new StoriesViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(StoriesViewHolder holder, int position) {
        holder.mTextView.setText(mBeanList.get(position).getTitle());
        if (mBeanList.get(position).getFirstImage() == null) {
            holder.mImageView.setImageResource(R.drawable.nullpicture);
        }else {
            Glide.with(mContext)
                    .load(mBeanList.get(position).getFirstImage())
                    .error(R.drawable.nullpicture)
                    .centerCrop()
                    .into(holder.mImageView);
        }
    }

    @Override
    public int getItemCount() {
        return mBeanList.size();
    }

    public void setItemClickListener(OnRecyclerOnClickListener listener) {
        mListener = listener;
    }

    class StoriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mImageView;
        private TextView mTextView;
        private OnRecyclerOnClickListener listener;

        public StoriesViewHolder(View itemView, OnRecyclerOnClickListener listener) {
            super(itemView);
            this.listener = listener;
            mImageView = (ImageView) itemView.findViewById(R.id.story_imageView);
            mTextView = (TextView) itemView.findViewById(R.id.story_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onItemClick(view, getLayoutPosition());
            }
        }
    }
}
