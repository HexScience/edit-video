package com.hecorat.azplugin2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Bkmsx on 12/12/2016.
 */

class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "VideoEditorDB";


    DBHelper (Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
