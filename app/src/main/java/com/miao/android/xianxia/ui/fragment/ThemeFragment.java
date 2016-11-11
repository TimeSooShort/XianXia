package com.miao.android.xianxia.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.miao.android.xianxia.R;
import com.miao.android.xianxia.adapter.ThemePager;
import com.miao.android.xianxia.bean.ThemeBean;
import com.miao.android.xianxia.util.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/15.
 */

public class ThemeFragment extends Fragment {

    private static final String TAG = "ThemeFragment";
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private RequestQueue queue;

    private List<ThemeBean> mBeanList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theme, container, false);
        initViews(view);
        load();
        return view;
    }

    private void load() {
        JsonObjectRequest themesRequest = new JsonObjectRequest(Request.Method.GET, Api.THEME,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (!response.getString("limit").isEmpty()) {
                        JSONArray array = response.getJSONArray("others");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            String name = object.getString("name");

                            ThemeBean bean = new ThemeBean(name);
                            mBeanList.add(bean);
                        }
                        ThemePager adapter = new ThemePager(getActivity().getSupportFragmentManager(), mBeanList);
                        mViewPager.setAdapter(adapter);
                        mTabLayout.setupWithViewPager(mViewPager);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(mTabLayout, getResources().getString(R.string.wrong_process), Snackbar.LENGTH_SHORT).show();
            }
        });
        themesRequest.setTag(TAG);
        queue.add(themesRequest);
    }

    private void initViews(View view) {
        mTabLayout = (TabLayout) view.findViewById(R.id.theme_tab);
        mViewPager = (ViewPager) view.findViewById(R.id.theme_viewPager);

    }

    @Override
    public void onStop() {
        super.onStop();

        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }
}
