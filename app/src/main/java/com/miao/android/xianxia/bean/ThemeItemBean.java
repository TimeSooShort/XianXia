package com.miao.android.xianxia.bean;

/**
 * Created by Administrator on 2016/10/15.
 */

public class ThemeItemBean {

    private String title;
    private String id;
    //private List<String> images;
    private String[] images;

    public ThemeItemBean(String id, String title, String[] images) {
        this.id = id;
        this.title = title;
        //this.imageItemUrlList = imageItemUrlList;
        this.images = images;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getFirstImage() {
        if (images == null)
            return null;
        return images[0];
    }
}
