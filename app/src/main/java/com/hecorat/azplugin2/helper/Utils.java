package com.hecorat.azplugin2.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.support.v4.provider.DocumentFile;
import android.util.DisplayMetrics;
import android.util.Log;

import com.hecorat.azplugin2.main.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by bkmsx on 11/11/2016.
 */
public class Utils {

    public static String getTime(){
        return new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
                .format(Calendar.getInstance().getTime());
    }

    public static void writeToFile(File fileTxt, String data) {
        try {
            FileWriter out = new FileWriter(fileTxt);
            out.write(data);
            out.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String getNameExtension() {
        return new SimpleDateFormat("_HH_mm_ss", Locale.getDefault())
                .format(new Date(System.currentTimeMillis()));
    }

    public static Bitmap createDefaultBitmap() {
        Paint paint = new Paint();
        Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);
        return bitmap;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int value;
        if (context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE){
            value = metrics.heightPixels;
        } else {
            value = metrics.widthPixels;
        }
        return value;
    }

    public static int dpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density);
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
        for (String fileName : fileList) {
            arrayList.add(directory + "/" + fileName);
        }
        return arrayList;
    }

    public static void copyFileFromAssets(Context context, String input, String output) {
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

    public static String getOutputFolder() {
        String direct = Environment.getExternalStorageDirectory().toString();
        String outputFolder = direct + "/" + Constants.OUTPUT_FOLDER;
        File file = new File(outputFolder);
        if (!file.exists()) {
            file.mkdirs();
        }
        return outputFolder;
    }

    public static String getTempFolder() {
        String direct = Environment.getExternalStorageDirectory().toString();
        String tempFolder = direct + "/" + Constants.OUTPUT_FOLDER + "/" + Constants.TEMP_FOLDER;
        File file = new File(tempFolder);
        if (!file.exists()) {
            file.mkdirs();
        }
        return tempFolder;
    }

    public static void deleteTempFiles() {
        File[] files = new File(Utils.getTempFolder()).listFiles();
        for (File file : files) {
            file.delete();
        }
    }

    public static String getResourceFolder() {
        String direct = Environment.getExternalStorageDirectory().toString();
        String resourceFolder = direct + "/" + Constants.OUTPUT_FOLDER + "/" + Constants.RESOURCE_FOLDER;
        File file = new File(resourceFolder);
        if (!file.exists()) {
            file.mkdirs();
        }
        return resourceFolder;
    }

    public static String getFontFolder() {
        String direct = Environment.getExternalStorageDirectory().toString();
        String resourceFolder = direct + "/" + Constants.OUTPUT_FOLDER + "/" + Constants.FONT_FOLDER;
        File file = new File(resourceFolder);
        if (!file.exists()) {
            file.mkdirs();
        }
        return resourceFolder;
    }

    public static SharedPreferences getSharedPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String timeToText(int second) {
        String pattern = "mm:ss";
        Date date = new Date(second % 3600 * 1000);
        String time = new SimpleDateFormat(pattern, Locale.getDefault()).format(date);
        if (second >= 3600) {
            time = second / 3600 + ":" + time;
        }
        return time;
    }

    public static String getSdPath(Context context) {
        File[] listDir = context.getExternalFilesDirs(Environment.DIRECTORY_MOVIES);
        if (listDir.length < 2) {
            return null;
        }
        String sdDir = listDir[1].getAbsolutePath();
        int index = sdDir.indexOf("/Android/data");
        return sdDir.substring(0, index);
    }

    public static boolean moveFile(Context context, String src, String dst,
                                   boolean useUri, boolean isVideo) {
        if (useUri) {
            try {
                FileInputStream is = new FileInputStream(src);
                FileChannel srcChannel = is.getChannel();
                String outputName = "hello.mp4";
                DocumentFile outputDir = DocumentFile.fromTreeUri(context,
                        Uri.parse(dst));
                String mediaType = isVideo ? "video/mp4" : "image/gif";
                DocumentFile outputFile = outputDir.createFile(mediaType,
                        outputName.substring(0, outputName.length() - 4));
                ParcelFileDescriptor parcelFd;

                parcelFd = context.getContentResolver().openFileDescriptor(
                        outputFile.getUri(), "rw");
                FileOutputStream os = new FileOutputStream(
                        parcelFd.getFileDescriptor());
                FileChannel dstChannel = os.getChannel();

                dstChannel.transferFrom(srcChannel, 0, srcChannel.size());

                srcChannel.close();
                is.close();
                dstChannel.close();
                os.close();
                parcelFd.closeWithError("error");
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            try {
                FileInputStream is = new FileInputStream(src);
                FileChannel srcChannel = is.getChannel();

                FileOutputStream os = new FileOutputStream(dst);
                FileChannel dstChannel = os.getChannel();
                dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
                srcChannel.close();
                is.close();
                dstChannel.close();
                os.close();

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        }
        return true;
    }
}
