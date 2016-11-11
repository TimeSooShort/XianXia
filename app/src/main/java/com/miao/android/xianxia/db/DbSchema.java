package com.miao.android.xianxia.db;

/**
 * Created by Administrator on 2016/10/18.
 */

public class DbSchema {

    public static final class LatestTable {
        public static final String NAME = "Latest";

        public static final class cols {
            public static final String TITLE = "title";
            public static final String IMAGE_URL = "image_url";
        }
    }

    public static final class ContentsTable {
        public static final String NAME = "Contents";

        public static final class cols {
            public static final String BODY = "body";
        }
    }
}
