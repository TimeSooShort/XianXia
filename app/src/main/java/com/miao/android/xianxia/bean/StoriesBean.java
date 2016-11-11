package com.miao.android.xianxia.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/10/12.
 */

public class StoriesBean {

    private String id;
    private String title;
    private List<String> images;

    public StoriesBean(String id, String title, List<String> images) {
        this.id = id;
        this.title = title;
        this.images = images;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getFirstImage() {
        if (images.isEmpty())
            return null;
        return images.get(0);
    }
}
