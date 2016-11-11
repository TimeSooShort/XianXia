package com.miao.android.xianxia.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2016/10/14.
 */

public class CustomItemDecoration extends RecyclerView.ItemDecoration {

    private final int orientation;
    private Drawable mDrawable;

    public CustomItemDecoration(Context context, int orientation) {
        TypedArray array = context.obtainStyledAttributes(new int[]{android.R.attr.listDivider});
        mDrawable = array.getDrawable(0);
        array.recycle();
        this.orientation = orientation;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left, top, right, bottom;
        if (orientation == LinearLayoutManager.VERTICAL) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)
                        child.getLayoutParams();
                top = parent.getBottom() + layoutParams.bottomMargin;
                bottom = top + mDrawable.getIntrinsicHeight();
                mDrawable.setBounds(left, top, right, bottom);
                mDrawable.draw(c);
            }
        }else {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)
                        child.getLayoutParams();
                left = parent.getRight() + layoutParams.rightMargin;
                right = left + mDrawable.getIntrinsicHeight();
                mDrawable.setBounds(left, top, right, bottom);
                mDrawable.draw(c);
            }

        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if (orientation == LinearLayoutManager.VERTICAL) {
            outRect.set(0, 0, 0, 15);
        }else {
            outRect.set(0, 0, 10, 0);
        }
    }
}
