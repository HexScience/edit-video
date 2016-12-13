package com.hecorat.editvideo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Bkmsx on 12/12/2016.
 */

public class ImageTable {
    String TABLE_NAME = "Image";
    String ID = "Id";
    String PROJECT_ID = "ProjectId";
    String PATH = "Path";
    String LEFT = "Left";
    String RIGHT = "Right";
    String IN_LAYOUT_IMAGE = "InLayoutImage";
    String ORDER_IN_LAYOUT = "OrderInLayout";
    String ORDER_IN_LIST = "OrderInList";
    String X = "x";
    String Y = "y";
    String SCALE = "Scale";
    String ROTATION = "Rotation";
    DBHelper mDbHelper;

    public ImageTable(Context context) {
        mDbHelper = new DBHelper(context);
    }

    public void createTable() {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        String sql = "create table " + TABLE_NAME + " ("+
                ID + " integer primary key, " +
                PROJECT_ID + " integer, " +
                PATH + " text, " +
                LEFT + " text, " +
                RIGHT + " text, " +
                IN_LAYOUT_IMAGE + " text, " +
                ORDER_IN_LAYOUT + " text, " +
                ORDER_IN_LIST + " text, " +
                X + " text, " +
                Y + " text, " +
                SCALE + " text, " +
                ROTATION + " text)";
        sqLiteDatabase.execSQL(sql);
    }

    public void dropTable() {
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        String sql = "drop table if exists " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql);
    }

    public void insertValue(ImageObject image){
        SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROJECT_ID, image.projectId);
        contentValues.put(PATH, image.path);
        contentValues.put(LEFT, image.left);
        contentValues.put(RIGHT, image.right);
        contentValues.put(IN_LAYOUT_IMAGE, image.inLayoutImage);
        contentValues.put(ORDER_IN_LAYOUT, image.orderInLayout);
        contentValues.put(ORDER_IN_LIST, image.orderInList);
        contentValues.put(X, image.x);
        contentValues.put(Y, image.y);
        contentValues.put(SCALE, image.scale);
        contentValues.put(ROTATION, image.rotation);
        sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
    }

    public ArrayList<ImageObject> getData(int projectId){
        SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();
        ArrayList<ImageObject> list = new ArrayList<>();
        String sql = "select * from " + TABLE_NAME + " where " + PROJECT_ID + " = " + projectId;
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        try {
            while (cursor.moveToNext()) {
                ImageObject image = new ImageObject();
                image.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ID)));
                image.projectId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(PROJECT_ID)));
                image.path = cursor.getString(cursor.getColumnIndex(PATH));
                image.left = cursor.getString(cursor.getColumnIndex(LEFT));
                image.right = cursor.getString(cursor.getColumnIndex(RIGHT));
                image.inLayoutImage = cursor.getString(cursor.getColumnIndex(IN_LAYOUT_IMAGE));
                image.orderInLayout = cursor.getString(cursor.getColumnIndex(ORDER_IN_LAYOUT));
                image.orderInList = cursor.getString(cursor.getColumnIndex(ORDER_IN_LIST));
                image.x = cursor.getString(cursor.getColumnIndex(X));
                image.y = cursor.getString(cursor.getColumnIndex(Y));
                image.scale = cursor.getString(cursor.getColumnIndex(SCALE));
                image.rotation = cursor.getString(cursor.getColumnIndex(ROTATION));
                list.add(image);
            }
        } finally {
            cursor.close();
        }
        return list;
    }


}
