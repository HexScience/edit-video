package com.hecorat.editvideo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Bkmsx on 12/12/2016.
 */

public class ProjectTable {
    String TABLE_NAME = "Project";
    String ID = "Id";
    String NAME = "Name";
    String DATA = "Data";
    String FIRST_VIDEO = "FirstVideo";
    DBHelper mDbHelper;

    public ProjectTable(Context context) {
        mDbHelper = new DBHelper(context);
    }

    public void createTable() {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        String sql = "create table if not exists " + TABLE_NAME + "(" +
                ID + " integer primary key, " +
                NAME + " text, " +
                FIRST_VIDEO + " text, " +
                DATA + " text)";
        sqLiteDatabase.execSQL(sql);
    }

    public void dropTable() {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        String sql = "drop table if exists " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
    }

    public long insertValue(String name, String data) {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(DATA, data);
        contentValues.put(FIRST_VIDEO, "");
        return sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public void updateFirstVideo(int id, String firstVideo) {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FIRST_VIDEO, firstVideo);
        sqLiteDatabase.update(TABLE_NAME, contentValues, ID + " =? ",new String[]{id+""});
    }

    public void deleteProject(int id) {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        String sql = "delete from " + TABLE_NAME +
                " where " + ID + " = " + id;
        sqLiteDatabase.execSQL(sql);
    }

    public ArrayList<ProjectObject> getData() {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();
        ArrayList<ProjectObject> list = new ArrayList<>();
        String sql = "select * from " + TABLE_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                ProjectObject project = new ProjectObject();
                project.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID)));
                project.name = cursor.getString(cursor.getColumnIndex(NAME));
                project.data = cursor.getString(cursor.getColumnIndex(DATA));
                project.firstVideo = cursor.getString(cursor.getColumnIndex(FIRST_VIDEO));
                list.add(project);
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    private void log(String msg) {
        Log.e("Project Table", msg);
    }
}
