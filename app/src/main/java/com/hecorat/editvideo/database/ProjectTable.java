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
    DBHelper mDbHelper;

    public ProjectTable(Context context) {
        mDbHelper = new DBHelper(context);
    }

    public void createTable() {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        String sql = "create table if not exists " + TABLE_NAME + "(" +
                ID + " integer primary key, " +
                NAME + " text, " +
                DATA + " text)";
        sqLiteDatabase.execSQL(sql);
    }

    public void dropTable() {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        String sql = "drop table if exists " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
    }

    public boolean insertValue(String name, String data) {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(DATA, data);
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public ArrayList<String> getData(String name) {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        String sql = "select * from " + TABLE_NAME + " where " + NAME + " = '" + name + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        cursor.moveToFirst();
        list.add(cursor.getString(cursor.getColumnIndex(ID)));
        list.add(cursor.getString(cursor.getColumnIndex(NAME)));
        list.add(cursor.getString(cursor.getColumnIndex(DATA)));
        return list;
    }

    private void log(String msg) {
        Log.e("Project Table", msg);
    }
}
