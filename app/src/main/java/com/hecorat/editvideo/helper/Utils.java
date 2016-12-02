package com.hecorat.editvideo.helper;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;
import android.provider.SyncStateContract;

import com.hecorat.editvideo.main.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by bkmsx on 11/11/2016.
 */
public class Utils {

    public static final int dpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp*density);
    }

    public static ArrayList<String> listFilesFromAssets(Context context, String directory) {
        Resources res = context.getResources(); //if you are in an activity
        AssetManager am = res.getAssets();
        String fileList[] = new String[0];
        try {
            fileList = am.list(directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i=0; i<fileList.length; i++){
            arrayList.add(directory+"/"+fileList[i]);
        }
        return arrayList;
    }

    public static void copyFileFromAssets(Context context, String input, String output){
        AssetManager assetManager = context.getAssets();
        InputStream in;
        OutputStream out;
        try {
            in = assetManager.open(input);
            out = new FileOutputStream(output);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (Exception e) {
        }
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
