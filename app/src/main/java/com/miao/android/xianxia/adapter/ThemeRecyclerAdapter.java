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
import com.miao.android.xianxia.bean.ThemeItemBean;
import com.miao.android.xianxia.interfaces.OnRecyclerOnClickListener;

import java.util.List;

/**
 * Created by Administrator on 2016/10/15.
 */

public class ThemeRecyclerAdapter extends RecyclerView.Adapter<ThemeRecyclerAdapter.ThemeViewHolder> {

    private Context context;
    private List<ThemeItemBean> mBeanList;
    private final LayoutInflater mInflater;

    private OnRecyclerOnClickListener mListener;


    public ThemeRecyclerAdapter(Context context, List<ThemeItemBean> themeItemBeen) {
        this.context = context;
        mBeanList = themeItemBeen;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ThemeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.fragment_storyitem, parent, false);
        return new ThemeViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(ThemeViewHolder holder, int position) {
        holder.mThemeText.setText(mBeanList.get(position).getTitle());
        if (mBeanList.get(position).getFirstImage() == null) {
            holder.mThemeImage.setVisibility(View.GONE);
        }else {
            Glide.with(context)
                    .load(mBeanList.get(position).getFirstImage())
                    .asBitmap()
                    .centerCrop()
                    .into(holder.mThemeImage);
        }

    }

    @Override
    public int getItemCount() {
        return mBeanList.size();
    }

    public void setItemClickListener(OnRecyclerOnClickListener listener) {
        mListener = listener;
    }

    class ThemeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private OnRecyclerOnClickListener listener;
        private ImageView mThemeImage;
        private TextView mThemeText;

        public ThemeViewHolder(View itemView, OnRecyclerOnClickListener listener) {
            super(itemView);
            this.listener = listener;
            mThemeImage = (ImageView) itemView.findViewById(R.id.story_imageView);
            mThemeText = (TextView) itemView.findViewById(R.id.story_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(view, getLayoutPosition());
        }
    }
}
