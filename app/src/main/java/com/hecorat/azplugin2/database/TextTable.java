package com.hecorat.azplugin2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Bkmsx on 12/12/2016.
 */

public class TextTable {
    private String TABLE_NAME = "Text";
    private String ID = "Id";
    private String PROJECT_ID = "ProjectId";
    private String TEXT = "Text";
    private String LEFT = "Left";
    private String RIGHT = "Right";
    private String IN_LAYOUT_IMAGE = "InLayoutImage";
    private String ORDER_IN_LAYOUT = "OrderInLayout";
    private String ORDER_IN_LIST = "OrderInList";
    private String X = "x";
    private String Y = "y";
    private String SCALE = "Scale";
    private String ROTATION = "Rotation";
    private String SIZE = "Size";
    private String FONT_PATH = "FontPath";
    private String FONT_COLOR = "FontColor";
    private String BOX_COLOR = "BoxColor";
    private String FONT_ID = "FontId";
    private DBHelper mDbHelper;

    public TextTable(Context context) {
        mDbHelper = new DBHelper(context);
    }

    public void createTable() {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        String sql = "create table if not exists " + TABLE_NAME + " ("+
                ID + " integer primary key, " +
                PROJECT_ID + " integer, " +
                TEXT + " text, " +
                LEFT + " text, " +
                RIGHT + " text, " +
                IN_LAYOUT_IMAGE + " text, " +
                ORDER_IN_LAYOUT + " text, " +
                ORDER_IN_LIST + " text, " +
                X + " text, " +
                Y + " text, " +
                SCALE + " text, " +
                ROTATION + " text, " +
                SIZE + " text, " +
                FONT_PATH + " text, " +
                FONT_COLOR + " text, " +
                BOX_COLOR + " text, " +
                FONT_ID + " text)";
        sqLiteDatabase.execSQL(sql);
    }

    public void dropTable() {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        String sql = "drop table if exists " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
    }

    public void insertValue(TextObject text, int projectId){
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROJECT_ID, projectId);
        contentValues.put(TEXT, text.text);
        contentValues.put(LEFT, text.left);
        contentValues.put(RIGHT, text.right);
        contentValues.put(IN_LAYOUT_IMAGE, text.inLayoutImage);
        contentValues.put(ORDER_IN_LAYOUT, text.orderInLayout);
        contentValues.put(ORDER_IN_LIST, text.orderInList);
        contentValues.put(X, text.x);
        contentValues.put(Y, text.y);
        contentValues.put(SCALE, text.scale);
        contentValues.put(ROTATION, text.rotation);
        contentValues.put(SIZE, text.size);
        contentValues.put(FONT_PATH, text.fontPath);
        contentValues.put(FONT_COLOR, text.fontColor);
        contentValues.put(BOX_COLOR, text.boxColor);
        contentValues.put(FONT_ID, text.fontId);
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public void deleteTextOf(int projectId) {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        String sql = "delete from " + TABLE_NAME +
                " where " + PROJECT_ID + " = " + projectId;
        sqLiteDatabase.execSQL(sql);
    }

    public ArrayList<TextObject> getData(int projectId){
        SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();
        ArrayList<TextObject> list = new ArrayList<>();
        String sql = "select * from " + TABLE_NAME + " where " + PROJECT_ID + " = " + projectId;
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                TextObject text = new TextObject();
                text.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID)));
                text.text = cursor.getString(cursor.getColumnIndex(TEXT));
                text.left = cursor.getString(cursor.getColumnIndex(LEFT));
                text.right = cursor.getString(cursor.getColumnIndex(RIGHT));
                text.inLayoutImage = cursor.getString(cursor.getColumnIndex(IN_LAYOUT_IMAGE));
                text.orderInLayout = cursor.getString(cursor.getColumnIndex(ORDER_IN_LAYOUT));
                text.orderInList = cursor.getString(cursor.getColumnIndex(ORDER_IN_LIST));
                text.x = cursor.getString(cursor.getColumnIndex(X));
                text.y = cursor.getString(cursor.getColumnIndex(Y));
                text.scale = cursor.getString(cursor.getColumnIndex(SCALE));
                text.rotation = cursor.getString(cursor.getColumnIndex(ROTATION));
                text.size = cursor.getString(cursor.getColumnIndex(SIZE));
                text.fontPath = cursor.getString(cursor.getColumnIndex(FONT_PATH));
                text.fontColor = cursor.getString(cursor.getColumnIndex(FONT_COLOR));
                text.boxColor = cursor.getString(cursor.getColumnIndex(BOX_COLOR));
                text.fontId = cursor.getString(cursor.getColumnIndex(FONT_ID));
                list.add(text);
            }
        } finally {
            cursor.close();
        }
        return list;
    }
}
