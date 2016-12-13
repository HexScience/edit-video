package com.hecorat.editvideo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Bkmsx on 12/12/2016.
 */

public class VideoTable {
    String TABLE_NAME = "Video";
    String ID = "Id";
    String PROJECT_ID = "ProjectId";
    String PATH = "Path";
    String START_TIME = "StartTime";
    String END_TIME = "EndTime";
    String LEFT = "Left";
    String ORDER = "_Order";
    DBHelper mDbHelper;

    public VideoTable(Context context) {
        mDbHelper = new DBHelper(context);
    }

    public void createTable() {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        String sql = "create table " + TABLE_NAME + " ("+
                ID + " integer primary key, " +
                PROJECT_ID + " integer, " +
                PATH + " text, " +
                START_TIME + " text, " +
                END_TIME + " text, " +
                LEFT + " text, " +
                ORDER + " text)";
        sqLiteDatabase.execSQL(sql);
    }

    public void dropTable() {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        String sql = "drop table if exists " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
    }

    public void insertValue(VideoObject video){
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROJECT_ID, video.projectId);
        contentValues.put(PATH, video.path);
        contentValues.put(START_TIME, video.startTime);
        contentValues.put(END_TIME, video.endTime);
        contentValues.put(LEFT, video.left);
        contentValues.put(ORDER, video.orderInList);
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public ArrayList<VideoObject> getData(int projectId){
        SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();
        ArrayList<VideoObject> list = new ArrayList<>();
        String sql = "select * from " + TABLE_NAME + " where " + PROJECT_ID + " = " + projectId;
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                VideoObject video = new VideoObject();
                video.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID)));
                video.projectId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(PROJECT_ID)));
                video.path = cursor.getString(cursor.getColumnIndex(PATH));
                video.startTime = cursor.getString(cursor.getColumnIndex(START_TIME));
                video.endTime = cursor.getString(cursor.getColumnIndex(END_TIME));
                video.left = cursor.getString(cursor.getColumnIndex(LEFT));
                video.orderInList = cursor.getString(cursor.getColumnIndex(ORDER));
                list.add(video);
            }
        } finally {
            cursor.close();
        }
        return list;
    }


}
