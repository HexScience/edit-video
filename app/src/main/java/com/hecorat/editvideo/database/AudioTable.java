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
    String VOLUME = "Volume";
    String VOLUME_PREVIEW = "VolumePreview";
    DBHelper mDbHelper;

    public AudioTable(Context context) {
        mDbHelper = new DBHelper(context);
    }

    public void createTable() {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        String sql = "create table if not exists " + TABLE_NAME + " ("+
                ID + " integer primary key, " +
                PROJECT_ID + " integer, " +
                PATH + " text, " +
                START_TIME + " text, " +
                END_TIME + " text, " +
                LEFT + " text, " +
                ORDER + " text, " +
                VOLUME + " text, " +
                VOLUME_PREVIEW + " text)";
        sqLiteDatabase.execSQL(sql);
    }

    public void dropTable() {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        String sql = "drop table if exists " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
    }

    public void insertValue(AudioObject audio){
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROJECT_ID, audio.projectId);
        contentValues.put(PATH, audio.path);
        contentValues.put(START_TIME, audio.startTime);
        contentValues.put(END_TIME, audio.endTime);
        contentValues.put(LEFT, audio.left);
        contentValues.put(ORDER, audio.orderInList);
        contentValues.put(VOLUME, audio.volume);
        contentValues.put(VOLUME_PREVIEW, audio.volumePreview);
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
                audio.path = cursor.getString(cursor.getColumnIndex(PATH));
                audio.startTime = cursor.getString(cursor.getColumnIndex(START_TIME));
                audio.endTime = cursor.getString(cursor.getColumnIndex(END_TIME));
                audio.left = cursor.getString(cursor.getColumnIndex(LEFT));
                audio.orderInList = cursor.getString(cursor.getColumnIndex(ORDER));
                audio.volume = cursor.getString(cursor.getColumnIndex(VOLUME));
                audio.volumePreview = cursor.getString(cursor.getColumnIndex(VOLUME_PREVIEW));
                list.add(audio);
            }
        } finally {
            cursor.close();
        }
        return list;
    }
}
