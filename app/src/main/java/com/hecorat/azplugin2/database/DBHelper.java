package com.hecorat.azplugin2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Bkmsx on 12/12/2016.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "VideoEditorDB";


    public DBHelper (Context context) {
        super(context, DB_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    private void log(String msg) {
        Log.e("DbHelper",msg);
    }

}
