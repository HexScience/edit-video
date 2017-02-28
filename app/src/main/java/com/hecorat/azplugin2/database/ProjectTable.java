package com.hecorat.azplugin2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Bkmsx on 12/12/2016.
 */

public class ProjectTable {
    private String TABLE_NAME = "Project";
    public static final String PROJECT_ID = "Id";
    public static final String PROJECT_NAME = "Name";
    public static final String PROJECT_DATA = "Data";
    public static final String PROJECT_FIRST_VIDEO = "FirstVideo";
    private DBHelper mDbHelper;

    public ProjectTable(Context context) {
        mDbHelper = new DBHelper(context);
    }

    public void createTable() {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        String sql = "create table if not exists " + TABLE_NAME + "(" +
                PROJECT_ID + " integer primary key, " +
                PROJECT_NAME + " text, " +
                PROJECT_FIRST_VIDEO + " text, " +
                PROJECT_DATA + " text)";
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
        contentValues.put(PROJECT_NAME, name);
        contentValues.put(PROJECT_DATA, data);
        contentValues.put(PROJECT_FIRST_VIDEO, "");
        return sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public void updateValue(int id, String col, String value) {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col, value);
        sqLiteDatabase.update(TABLE_NAME, contentValues, PROJECT_ID + " =? ",new String[]{id+""});
    }

    public void deleteProject(int id) {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        String sql = "delete from " + TABLE_NAME +
                " where " + PROJECT_ID + " = " + id;
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
                project.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(PROJECT_ID)));
                project.name = cursor.getString(cursor.getColumnIndex(PROJECT_NAME));
                project.data = cursor.getString(cursor.getColumnIndex(PROJECT_DATA));
                project.firstVideo = cursor.getString(cursor.getColumnIndex(PROJECT_FIRST_VIDEO));
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

    public Cursor queryAllRecentProject() throws SQLException {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        String selection = "SELECT * FROM " + TABLE_NAME + " ORDER BY "
                + PROJECT_ID + " DESC";
        return database.rawQuery(selection, null);
    }

    public ArrayList<ProjectObject> getRecentProjectsFromCursor(Cursor cursor) {
        ArrayList<ProjectObject> recentProjectsList = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                ProjectObject project = new ProjectObject();
                project.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(PROJECT_ID)));
                project.name = cursor.getString(cursor.getColumnIndex(PROJECT_NAME));
                project.data = cursor.getString(cursor.getColumnIndex(PROJECT_DATA));
                project.firstVideo = cursor.getString(cursor.getColumnIndex(PROJECT_FIRST_VIDEO));
                recentProjectsList.add(project);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return recentProjectsList;
    }
}
