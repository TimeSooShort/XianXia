package com.miao.android.xianxia.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.miao.android.xianxia.R;
import com.miao.android.xianxia.db.DataBaseHelper;
import com.miao.android.xianxia.util.Api;
import com.miao.android.xianxia.util.NetworkState;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/10/12.
 */

public class ContentActivity extends AppCompatActivity {

    private CollapsingToolbarLayout mCollapseToolBar;
    private ImageView mImageView;
    private TextView mTitle;
    private WebView mWebView;
    private FloatingActionButton actionButton;
    private Toolbar mToolbar;

    private String id;
    private String imageUrl;
    private String title;
    private RequestQueue queue;
    private int likes = 0;
    private int comments = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        initViews();

        queue = Volley.newRequestQueue(getApplicationContext());

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        imageUrl = intent.getStringExtra("imageUrl");
        title = intent.getStringExtra("title");

        mCollapseToolBar.setTitle(title);

        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mWebView.loadUrl(url);
                return true;
            }
        });
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                    mWebView.goBack();
                    return true;
                }
                return false;
            }
        });

        if (!NetworkState.networkConneted(ContentActivity.this)) {
            mImageView.setImageResource(R.drawable.nullpicture);
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            SQLiteDatabase contentDatabase = new DataBaseHelper(this).getWritableDatabase();
            Cursor cursor = contentDatabase.query("Contents", null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    if (id.equals(String.valueOf(cursor.getInt(cursor.getColumnIndex("_id"))))){
                        String body = cursor.getString(cursor.getColumnIndex("body"));

                        String html = "<link rel=\"stylesheet\" type=\"text/css href=\"file:///android_asset/zhihu_master.css\">" + body;
                        mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
                    }
                }while (cursor.moveToNext());
            }
            cursor.close();
        }else {
            loadContent();
        }
    }

    private void loadContent() {
        /**
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }*/

        JsonObjectRequest contentRequest = new JsonObjectRequest(Request.Method.GET,
                Api.CONTENT + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.isNull("body")) {
                        String share_url = response.getString("share_url");
                        mWebView.loadUrl(share_url);
                        mImageView.setImageResource(R.drawable.nullpicture);
                        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    } else {
                        if (!response.isNull("image")) {
                            Glide.with(ContentActivity.this)
                                    .load(response.getString("image"))
                                    .centerCrop()
                                    .into(mImageView);
                            mTitle.setText(response.getString("image_source"));
                        } else if (imageUrl == null) {
                            mImageView.setImageResource(R.drawable.nullpicture);
                            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        } else {
                            Glide.with(ContentActivity.this)
                                    .load(imageUrl)
                                    .centerCrop()
                                    .into(mImageView);
                        }

                        //加载网络CSS，此种方式中文章内容中的图片会很好的适应屏幕大小

                        String body = response.getString("body").replace("<div class=\"img-place-holder\">", "");
                        body = body.replace("< div class=\"headline\">", "");
                        String css = response.getJSONArray("css").getString(0);
                        String html = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + css + "\">" + body;

                        //加载本地CSS
                        /**
                        String head = "<head><style>img{max-width: 100%; width:auto; height: auto;}</style></head>";
                        String body = response.getString("body").replace("<div class=\"img-place-holder\">", "");
                        body = body.replace("<div class=\"headline\">", "");
                        String css = "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/zhihu_master.css\">" ;
                        String html = "<html>" + head + css + "<body>" + body + "</body></html>";*/

                        mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(actionButton, R.string.wrong_process, Snackbar.LENGTH_SHORT).show();
            }
        });
        queue.add(contentRequest);

        JsonObjectRequest extralRequest = new JsonObjectRequest(Request.Method.GET,
                Api.STORY_EXTRAL + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    likes = response.getInt("popularity");
                    comments = response.getInt("comments");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(actionButton, R.string.wrong_process, Snackbar.LENGTH_SHORT);
            }
        });
        queue.add(extralRequest);
    }

    private void initViews() {
        mCollapseToolBar = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        mImageView = (ImageView) findViewById(R.id.content_image);
        mTitle = (TextView) findViewById(R.id.copy_text);
        mWebView = (WebView) findViewById(R.id.webView);
        actionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);

        mToolbar = (Toolbar) findViewById(R.id.content_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_content, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        String contentComments = getResources().getString(R.string.pinglun) + ": " + comments;
        menu.findItem(R.id.pinglun).setTitle(contentComments);
        String contentLikes = getResources().getString(R.string.zan) + ": " + likes;
        menu.findItem(R.id.zan).setTitle(contentLikes);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            onBackPressed();
        }

        /**
        if (id == R.id.pinglun) {
            startActivity(new Intent(ContentActivity.this, CommentsActivity.class).putExtra("id", id));
        }*/
        return super.onOptionsItemSelected(item);
    }
}
