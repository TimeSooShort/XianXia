package com.miao.android.xianxia.bean;

/**
 * Created by Administrator on 2016/10/14.
 */

public class CommentBean {

    private String author;
    private String content;
    private int time;
    private String avatar;

    public CommentBean(String author, String content, int time, String avatar) {
        this.author = author;
        this.content = content;
        this.time = time;
        this.avatar = avatar;
    }

    public String getAuthor() {
        return author;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getContent() {
        return content;
    }

    public int getTime() {
        return time;
    }
}
