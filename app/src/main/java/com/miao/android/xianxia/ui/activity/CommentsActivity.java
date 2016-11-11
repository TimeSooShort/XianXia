package com.miao.android.xianxia.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.miao.android.xianxia.R;
import com.miao.android.xianxia.bean.CommentBean;
import com.miao.android.xianxia.util.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/14.
 */

public class CommentsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private RequestQueue queue;
    private int id;

    private List<CommentBean> mBeanList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_comment);

        initViews();

        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);

        queue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest commentRequest = new JsonObjectRequest(Request.Method.GET,
                Api.COMMENT + id + "/long-comments", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray commentArray = response.getJSONArray("comments");
                    if (commentArray.length() != 0) {
                        for (int i = commentArray.length(); i >= 0; i--) {
                            JSONObject object = commentArray.getJSONObject(i);
                            CommentBean bean = new CommentBean(object.getString("author"),
                                    object.getString("content"), object.getInt("time"),
                                    object.getString("avatar"));
                            mBeanList.add(bean);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(commentRequest);

        JsonObjectRequest shortRequest = new JsonObjectRequest(Request.Method.GET,
                Api.COMMENT + "/" + "short-comments", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray shortArray = response.getJSONArray("comments");
                    if (shortArray.length() != 0) {
                        for (int j = shortArray.length(); j >= 0; j--) {
                            JSONObject shortObject = shortArray.getJSONObject(j);
                            CommentBean shortBean = new CommentBean(shortObject.getString("author"),
                                    shortObject.getString("content"), shortObject.getInt("time"),
                                    shortObject.getString("avatar"));
                            mBeanList.add(shortBean);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(shortRequest);
    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.comment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) findViewById(R.id.comment_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
    }
}
