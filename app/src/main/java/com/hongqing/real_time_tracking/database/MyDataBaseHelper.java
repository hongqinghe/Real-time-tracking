package com.hongqing.real_time_tracking.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 贺红清 on 2017/2/7.
 */

public class MyDataBaseHelper extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "track.db2";
    public static String TABLE_TRACK = "track2";
    public static String TABLE_TRACK_DETAILS = "track_detail2";


    public static String ID = "id";

    public static String TRACK_NAME = "track_name";
    public static String CREATE_DATE = "create_date";
    public static String START_LOC = "start_loc";
    public static String END_LOC = "end_loc";
    public static String TID = "tid";//外键（不能不空）
    public static String LAT = "lat";
    public static String LNG = "lng";

    private static String CREATE_TABLE_TRACK = "create table track2(" +
            "id integer primary key autoincrement ," +
            "track_name text," +
            "create_date text," +
            "start_loc text,end_loc text)";
    private static String CREATE_TABLE_TRACK_DETAIL = "create table track_detail2(" +
            "id integer primary key autoincrement," +
            "tid integer not null," +
            "lat real," +
            "lng real)";
    private static int version = 1;

    public MyDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TRACK);
        db.execSQL(CREATE_TABLE_TRACK_DETAIL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
