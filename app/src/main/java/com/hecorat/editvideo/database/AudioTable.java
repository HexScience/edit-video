package com.hecorat.editvideo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Bkmsx on 12/12/2016.
 */

public class AudioTable {
    String TABLE_NAME = "Audio";
    String ID = "Id";
    String PROJECT_ID = "ProjectId";
    String PATH = "Path";
    String START_TIME = "StartTime";
    String END_TIME = "EndTime";
    String LEFT = "Left";
    String ORDER = "_Order";
    DBHelper mDbHelper;

    public AudioTable(Context context) {
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

    public ArrayList<AudioObject> getData(int projectId){
        SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();
        ArrayList<AudioObject> list = new ArrayList<>();
        String sql = "select * from " + TABLE_NAME + " where " + PROJECT_ID + " = " + projectId;
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                AudioObject audio = new AudioObject();
                audio.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID)));
                audio.projectId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(PROJECT_ID)));
                audio.startTime = cursor.getString(cursor.getColumnIndex(START_TIME));
                audio.left = cursor.getString(cursor.getColumnIndex(LEFT));
                audio.order = cursor.getString(cursor.getColumnIndex(ORDER));
                list.add(audio);
            }
        } finally {
            cursor.close();
        }
        return list;
    }


}
