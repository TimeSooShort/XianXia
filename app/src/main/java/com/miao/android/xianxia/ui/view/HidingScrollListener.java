package com.miao.android.xianxia.ui.view;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Administrator on 2016/10/15.
 */

public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {

    private static final int HIDE_THRESHOLD = 20;
    private int scrollDistance = 0;
    private boolean controlVisible = true;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int firstItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                .findFirstVisibleItemPosition();

        if (controlVisible && (scrollDistance > HIDE_THRESHOLD)){
            hide();
            controlVisible = false;
            scrollDistance = 0;
        }else if (!controlVisible && (scrollDistance < -HIDE_THRESHOLD) && (firstItemPosition == 0)) {
            show();
            controlVisible = true;
            scrollDistance = 0;
        }
        if ((controlVisible && dy > 0) || (!controlVisible && dy < 0 && firstItemPosition == 0)) {
            scrollDistance += dy;
        }
    }

    public abstract void show();
    public abstract void hide();

}
