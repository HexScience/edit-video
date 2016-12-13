package com.hecorat.editvideo.main;

import android.database.sqlite.SQLiteOpenHelper;

import com.clough.android.androiddbviewer.ADBVApplication;
import com.hecorat.editvideo.database.DBHelper;

/**
 * Created by Bkmsx on 12/12/2016.
 */

public class CustomApplication extends ADBVApplication {

    @Override
    public SQLiteOpenHelper getDataBase() {
        return new DBHelper(getApplicationContext());
    }

}
