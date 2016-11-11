package com.miao.android.xianxia.ui.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.miao.android.xianxia.adapter.StoriesRecyclerAdapter;
import com.miao.android.xianxia.bean.StoriesBean;
import com.miao.android.xianxia.db.DataBaseHelper;
import com.miao.android.xianxia.interfaces.OnRecyclerOnClickListener;
import com.miao.android.xianxia.ui.activity.ContentActivity;
import com.miao.android.xianxia.ui.view.CustomItemDecoration;
import com.miao.android.xianxia.util.Api;
import com.miao.android.xianxia.util.NetworkState;
import com.rey.material.app.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2016/10/12.
 */

public class LatestFragment extends Fragment {

    private static final Object TAG = "LatestFragment";
    private SwipeRefreshLayout refresh;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private RequestQueue queue;
    private List<StoriesBean> storiesBeanList = new ArrayList<>();

    private int year = 2013;
    private int month = 5;
    private int day = 20;

    private int slideCount = 0;
    private StoriesRecyclerAdapter adapter;
    private SQLiteDatabase mDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        mDatabase = new DataBaseHelper(getActivity()).getWritableDatabase();

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_latest, container, false);
        initViews(view);

        if (!NetworkState.networkConneted(getActivity())) {
            showNoNetwork();
            loadFromDB();
        }else {
            load();
        }

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!storiesBeanList.isEmpty()){
                    storiesBeanList.clear();
                }
                adapter.notifyDataSetChanged();
                load();

                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH) + 1;
                day = calendar.get(Calendar.DAY_OF_MONTH);

                slideCount = 0;
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            boolean isSlidingToLast = false;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = manager.getItemCount();
                int latestItemPosition = manager.findLastCompletelyVisibleItemPosition();
                if (latestItemPosition == (totalItemCount - 1) && isSlidingToLast) {
                    loadMore();
                }

                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                isSlidingToLast = dy > 0;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatePickerDialog dialog = new DatePickerDialog(getActivity());
                dialog.date(day, month - 1, year);
                Calendar calendar = Calendar.getInstance();
                calendar.set(2013,5,20);
                dialog.dateRange(calendar.getTimeInMillis(),Calendar.getInstance().getTimeInMillis());
                dialog.show();
                dialog.positiveAction(getString(R.string.dialog_positive));
                dialog.negativeAction(getString(R.string.dialog_negative));

                dialog.positiveActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.negativeActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

        return view;
    }

    private void loadMore() {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day - slideCount);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        final String date = format.format(cal.getTime());

        JsonObjectRequest loadMoreRequest = new JsonObjectRequest(Request.Method.GET,
                Api.HISTORY + date, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray moreStoriesArray = response.getJSONArray("stories");
                    for (int i = 0; i < moreStoriesArray.length(); i++) {
                        List<String> moreImageUrlList = new ArrayList<>();
                        String title = moreStoriesArray.getJSONObject(i).getString("title");
                        String id = moreStoriesArray.getJSONObject(i).getString("id");
                        JSONArray moreImageArray = moreStoriesArray.getJSONObject(i).getJSONArray("images");
                        for (int j = 0; j < moreImageArray.length(); j++) {
                            String imageUrl = (String) moreImageArray.get(j);
                            moreImageUrlList.add(imageUrl);
                        }
                        StoriesBean moreBean = new StoriesBean(id, title, moreImageUrlList);
                        storiesBeanList.add(moreBean);
                    }

                    adapter.notifyDataSetChanged();
                    slideCount++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        loadMoreRequest.setTag(TAG);
        queue.add(loadMoreRequest);
    }

    private void load() {

        refresh.post(new Runnable() {
            @Override
            public void run() {
                refresh.setRefreshing(true);
            }
        });

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, Api.LATEST, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (!response.getString("date").isEmpty()){
                                JSONArray storiesArray = response.getJSONArray("stories");
                                for (int i = 0; i < storiesArray.length(); i++) {
                                    JSONObject storyItem = storiesArray.getJSONObject(i);
                                    String title = storyItem.getString("title");
                                    String id = storyItem.getString("id");
                                    JSONArray imageArray = storyItem.getJSONArray("images");
                                    List<String> imageUrlLists = new ArrayList<>();
                                    for (int j = 0; j < imageArray.length(); j++) {
                                        String imageUrl = imageArray.getString(j);
                                        imageUrlLists.add(imageUrl);
                                    }

                                    StoriesBean bean = new StoriesBean(id, title, imageUrlLists);
                                    storiesBeanList.add(bean);

                                    if (!queryIDExists("Latest", id)) {
                                        ContentValues value = new ContentValues();
                                        value.put("_id", Integer.valueOf(id));
                                        value.put("title", title);
                                        value.put("image_url", imageUrlLists.get(0));

                                        storeContent(id);

                                        mDatabase.insert("Latest", null, value);

                                        value.clear();
                                    }
                                }
                            }

                            adapter = new StoriesRecyclerAdapter(getActivity(), storiesBeanList);
                            recyclerView.setAdapter(adapter);
                            adapter.setItemClickListener(new OnRecyclerOnClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    Intent intent = new Intent(getActivity(), ContentActivity.class);
                                    intent.putExtra("id", storiesBeanList.get(position).getId());
                                    intent.putExtra("imageUrl", storiesBeanList.get(position).getFirstImage());
                                    intent.putExtra("title", storiesBeanList.get(position).getTitle());
                                    startActivity(intent);
                                }
                            });

                            if (refresh.isRefreshing()){

                                refresh.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        refresh.setRefreshing(false);
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
                if (refresh.isRefreshing()){
                    Snackbar.make(fab, R.string.wrong_process,Snackbar.LENGTH_SHORT).show();
                    refresh.post(new Runnable() {
                        @Override
                        public void run() {
                            refresh.setRefreshing(false);
                        }
                    });
                }
            }
        });
        request.setTag(TAG);
        queue.add(request);
    }

    private void storeContent(final String id) {
        JsonObjectRequest noNetworkRequest = new JsonObjectRequest(Request.Method.GET,
                Api.CONTENT + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (queryIDExists("Latest", id)) {
                    try {
                        if (!response.isNull("body")) {
                            String body = response.getString("body").replace("<div class=\"img-place-holder\">", "");
                            body = body.replace("<div class=\"headline\">", "");

                            ContentValues value = new ContentValues();
                            value.put("_id", Integer.valueOf(id));
                            value.put("body", body);

                            mDatabase.insert("Contents", null, value);
                            value.clear();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        noNetworkRequest.setTag(TAG);
        queue.add(noNetworkRequest);
    }

    private boolean queryIDExists(String tableName, String id) {
        Cursor cursor = mDatabase.query(tableName, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                if (id.equals(String.valueOf(cursor.getInt(cursor.getColumnIndex("_id"))))) {
                    return true;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();

        return false;
    }

    private void initViews(View view) {
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new CustomItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        refresh.setDistanceToTriggerSync(300);
        refresh.setProgressBackgroundColorSchemeColor(Color.BLUE);
        refresh.setSize(SwipeRefreshLayout.DEFAULT);
    }

    private void showNoNetwork() {
        Snackbar.make(fab, R.string.no_network, Snackbar.LENGTH_SHORT)
                .setAction(R.string.go_to_set, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                }).show();
    }

    private void loadFromDB() {
        Cursor cursor = mDatabase.query("Latest", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String id = String.valueOf(cursor.getInt(cursor.getColumnIndex("_id")));
                List<String> list = new ArrayList<>();
                list.add(cursor.getString(cursor.getColumnIndex("image_url")));

                StoriesBean bean = new StoriesBean(id, title, list);
                storiesBeanList.add(bean);
            }while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new StoriesRecyclerAdapter(getActivity(), storiesBeanList);
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(new OnRecyclerOnClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), ContentActivity.class);
                intent.putExtra("id", storiesBeanList.get(position).getId());
                intent.putExtra("title", storiesBeanList.get(position).getTitle());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        if (queue != null){
            queue.cancelAll(TAG);
        }

        if (refresh.isRefreshing()){
            refresh.setRefreshing(false);
        }
    }
}
