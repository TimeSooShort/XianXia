package com.miao.android.xianxia.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.miao.android.xianxia.R;
import com.miao.android.xianxia.adapter.ThemeRecyclerAdapter;
import com.miao.android.xianxia.bean.ThemeItemBean;
import com.miao.android.xianxia.interfaces.OnRecyclerOnClickListener;
import com.miao.android.xianxia.ui.activity.ContentActivity;
import com.miao.android.xianxia.ui.view.CustomItemDecoration;
import com.miao.android.xianxia.ui.view.HidingScrollListener;
import com.miao.android.xianxia.util.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/15.
 */

public class PageFragment extends Fragment {

    private static final String ARGS_PAGE = "args_page";
    private RelativeLayout header;
    private ImageView mImageView;
    private TextView mImageTitle;
    private RecyclerView mRecycler;

    private List<String> idList = new ArrayList<>();
    private int pages;
    private RequestQueue queue;
    private static final String TAG = "PagerFragment";

    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARGS_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pages = getArguments().getInt(ARGS_PAGE);
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pager, container, false);
        initViews(view);

        loadTheme();

        return view;
    }

    private void loadTheme() {
        JsonObjectRequest themeRequest = new JsonObjectRequest(Request.Method.GET, Api.THEME,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (!response.getString("limit").isEmpty()) {
                        JSONArray othersArray = response.getJSONArray("others");
                        for (int i = 0; i < othersArray.length(); i++) {
                            String id = othersArray.getJSONObject(i).getString("id");

                            idList.add(id);
                        }

                        final List<ThemeItemBean> themeItemBeanList = new ArrayList<>();
                        JsonObjectRequest themeContent = new JsonObjectRequest(Request.Method.GET,
                                Api.THEME_CONTENT + idList.get(pages), null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.has("stories")) {
                                        Glide.with(getActivity()).load(response.getString("image")).centerCrop().into(mImageView);
                                        mImageTitle.setText(response.getString("description"));

                                        JSONArray themeItemArray = response.getJSONArray("stories");
                                        for (int i = 0; i < themeItemArray.length(); i++) {
                                            JSONObject themeObject = themeItemArray.getJSONObject(i);
                                            //List<String> itemImageUrlList = new ArrayList<>();
                                            String[] strings;
                                            if (themeObject.isNull("images")) {
                                                strings = null;
                                            } else {
                                                strings = new String[themeObject.getJSONArray("images").length()];
                                                JSONArray imageUrlArray = themeObject.getJSONArray("images");
                                                for (int j = 0; j < imageUrlArray.length(); j++) {
                                                    strings[j] = imageUrlArray.getString(j);
                                                    //itemImageUrlList.add(itemImageUrl);
                                                }
                                            }
                                            String title = themeObject.getString("title");
                                            String id = themeObject.getString("id");

                                            ThemeItemBean itemBean = new ThemeItemBean(id, title, strings);
                                            themeItemBeanList.add(itemBean);
                                        }
                                        ThemeRecyclerAdapter adapter = new ThemeRecyclerAdapter(getActivity(), themeItemBeanList);
                                        mRecycler.setAdapter(adapter);
                                        adapter.setItemClickListener(new OnRecyclerOnClickListener() {
                                            @Override
                                            public void onItemClick(View view, int position) {
                                                Intent intent = new Intent(getActivity(), ContentActivity.class);
                                                intent.putExtra("id", themeItemBeanList.get(position).getId());
                                                intent.putExtra("title", themeItemBeanList.get(position).getTitle());
                                                intent.putExtra("imageUrl", themeItemBeanList.get(position).getFirstImage());
                                                startActivity(intent);
                                            }
                                        });
                                        mRecycler.addOnScrollListener(new HidingScrollListener() {
                                            @Override
                                            public void show() {
                                                showViews();
                                            }

                                            @Override
                                            public void hide() {
                                                hideViews();
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Snackbar.make(mImageView, getResources().getString(R.string.wrong_process), Snackbar.LENGTH_SHORT).show();
                            }
                        });
                        queue.add(themeContent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(mImageView, getResources().getString(R.string.wrong_process), Snackbar.LENGTH_SHORT).show();
            }
        });
        themeRequest.setTag(TAG);
        queue.add(themeRequest);
    }


        /**
        final List<ThemeItemBean> themeItemBeanList = new ArrayList<>();
        JsonObjectRequest themeContent = new JsonObjectRequest(Request.Method.GET,
                Api.THEME_CONTENT + idList.get(pages), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("stories")) {
                        Glide.with(getActivity()).load(response.getString("image")).centerCrop().into(mImageView);
                        mImageTitle.setText(response.getString("description"));

                        JSONArray themeItemArray = response.getJSONArray("stories");
                        for (int i = 0; i < themeItemArray.length(); i++) {
                            JSONObject themeObject = themeItemArray.getJSONObject(i);
                            List<String> itemImageUrlList = new ArrayList<>();
                            if (themeObject.isNull("images")) {
                                return;
                            } else {
                                JSONArray imageUrlArray = themeObject.getJSONArray("images");
                                for (int j = 0; j < imageUrlArray.length(); j++) {
                                    String itemImageUrl = (String) imageUrlArray.get(j);
                                    itemImageUrlList.add(itemImageUrl);
                                }
                            }
                            String title = themeObject.getString("title");
                            String id = themeObject.getString("id");

                            ThemeItemBean itemBean = new ThemeItemBean(id, title, itemImageUrlList);
                            themeItemBeanList.add(itemBean);
                        }
                        ThemeRecyclerAdapter adapter = new ThemeRecyclerAdapter(getActivity(), themeItemBeanList);
                        mRecycler.setAdapter(adapter);
                        adapter.setItemClickListener(new OnRecyclerOnClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(getActivity(), ContentActivity.class);
                                intent.putExtra("id", themeItemBeanList.get(position).getId());
                                intent.putExtra("title", themeItemBeanList.get(position).getTitle());
                                intent.putExtra("imageUrl", themeItemBeanList.get(position).getFirstImage());
                                startActivity(intent);
                            }
                        });
                        mRecycler.addOnScrollListener(new HidingScrollListener() {
                            @Override
                            public void show() {
                                showViews();
                            }

                            @Override
                            public void hide() {
                                hideViews();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(mImageView, getResources().getString(R.string.wrong_process), Snackbar.LENGTH_SHORT).show();
            }
        });
        themeContent.setTag(TAG);
        queue.add(themeContent);
    }*/

    private void showViews() {
        header.animate().translationY(0).setInterpolator(new LinearInterpolator());
    }

    private void hideViews() {
        header.animate().translationY(-header.getHeight()).setInterpolator(new LinearInterpolator());
    }

    private void initViews(View view) {
        header = (RelativeLayout) view.findViewById(R.id.header);
        mImageView = (ImageView) view.findViewById(R.id.theme_imageView);
        mImageTitle = (TextView) view.findViewById(R.id.image_title);
        mRecycler = (RecyclerView) view.findViewById(R.id.theme_recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.addItemDecoration(new CustomItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

    }

    @Override
    public void onStop() {
        super.onStop();

        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }
}
