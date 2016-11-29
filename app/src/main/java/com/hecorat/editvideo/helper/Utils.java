package com.hecorat.editvideo.helper;

import android.content.Context;
import android.os.Environment;
import android.provider.SyncStateContract;

import com.hecorat.editvideo.main.Constants;

import java.io.File;

/**
 * Created by bkmsx on 11/11/2016.
 */
public class Utils {

    public static final int dpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp*density);
    }

    public static String getOutputFolder(){
        String direct = Environment.getExternalStorageDirectory().toString();
        String outputFolder = direct+"/"+ Constants.OUTPUT_FOLDER;
        File file = new File(outputFolder);
        if (!file.exists()){
            file.mkdirs();
        }
        return outputFolder;
    }

    public static String getTempFolder(){
        String direct = Environment.getExternalStorageDirectory().toString();
        String tempFolder = direct+"/"+ Constants.OUTPUT_FOLDER+"/"+Constants.TEMP_FOLDER;
        File file = new File(tempFolder);
        if (!file.exists()){
            file.mkdirs();
        }
        return tempFolder;
    }
}
