package com.miao.android.xianxia.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.miao.android.xianxia.db.DbSchema.ContentsTable;
import com.miao.android.xianxia.db.DbSchema.LatestTable;

/**
 * Created by Administrator on 2016/10/21.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "storeBase.db";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table " + LatestTable.NAME + "(" +
                "_id integer primary key, " +
                LatestTable.cols.IMAGE_URL + ", " +
                LatestTable.cols.TITLE +
                ")"
        );

        sqLiteDatabase.execSQL("create table " + ContentsTable.NAME + "(" +
                "_id integer primary key, " +
                ContentsTable.cols.BODY +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
