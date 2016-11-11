package com.miao.android.xianxia.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.miao.android.xianxia.bean.ThemeBean;
import com.miao.android.xianxia.ui.fragment.PageFragment;

import java.util.List;

/**
 * Created by Administrator on 2016/10/15.
 */

public class ThemePager extends FragmentPagerAdapter {

    private List<ThemeBean> mThemeBeanList;

    public ThemePager(FragmentManager fm, List<ThemeBean> themeBeanList) {
        super(fm);
        mThemeBeanList = themeBeanList;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return mThemeBeanList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mThemeBeanList.get(position).getName();
    }
}
