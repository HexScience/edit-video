package com.hecorat.azplugin2.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.hecorat.azplugin2.IAzFFmpegService2;
import com.hecorat.azplugin2.R;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.LinkedList;

public class AzFFmpegService2 extends Service {
    private static final String CPU_X86 = "x86";
    private static final String CPU_ARMEABI_V7A = "armeabi-v7a";
    private static final String CPU_ARMEABI_V7A_NEON = "armeabi-v7a-neon";
    private static final String FFMPEG = "ffmpeg";
    private static String mFfmpegPath;
    private static final String RESULT = "result";
    private static final int RESULT_OK = 1;
    private static final int RESULT_ERROR = 0;
    private int mAzRecorderVersion = 34;

    private static String mLineLog = "";
    private static String mAllLog ="";

    @Override
    public void onCreate() {
        super.onCreate();
        checkUpdateVersion();
        initFFMPEG(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    final IAzFFmpegService2.Stub mBinder = new IAzFFmpegService2.Stub() {

        @Override
        public Bundle convertVideoToGif(String inputFile, String outputFile,
                                        int fps, int scale, int start, int duration, int loop)
                throws RemoteException {
            Bundle result = new Bundle();
            boolean success = performConvertVideoToGif(inputFile, outputFile,
                    fps, scale, start, duration, loop);

            result.putInt(RESULT, success ? RESULT_OK : RESULT_ERROR);
            return result;
        }

        @Override
        public int getPluginVersion() throws RemoteException {
            SharedPreferences sharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(AzFFmpegService2.this);
            return sharedPrefs.getInt(
                    getString(R.string.pref_app_version_code), 1);

        }

        @Override
        public void setAzRecorderVersion(int versionCode)
                throws RemoteException {
            mAzRecorderVersion = versionCode;
        }

        @Override
        public Bundle cropVideo(String input, String output, int width,
                                int height, int x, int y, int quality) {
            Bundle result = new Bundle();

            boolean success = performCropVideo(input, output, width, height, x,
                    y, quality);

            result.putInt(RESULT, success ? RESULT_OK : RESULT_ERROR);
            return result;
        }

        @Override
        public Bundle trimVideo(String inputFile, String outputFile,
                                String begin, String end) {
            Bundle result = new Bundle();
            boolean success = performTrimVideo(inputFile, outputFile, begin,
                    end);
            result.putInt(RESULT, success ? RESULT_OK : RESULT_ERROR);
            return result;
        }

        @Override
        public Bundle concatVideo(String inputFile, String outputFile) {
            Bundle result = new Bundle();
            boolean success = performConcatVideo(inputFile, outputFile);
            result.putInt(RESULT, success ? RESULT_OK : RESULT_ERROR);
            return result;
        }

        @Override
        public String getLineLog() {
            return performGetLineLog();
        }

        @Override
        public Bundle convertToAAC(String inputFile, String outputFile,
                                   String start, String end, boolean isAAC) {
            Bundle result = new Bundle();
            boolean value = performConvertToAAC(inputFile, outputFile, start,
                    end, isAAC);
            result.putInt(RESULT, value ? RESULT_OK : RESULT_ERROR);
            return result;
        }

        @Override
        public Bundle loopAudio(String inputText, String outputFile,
                                String duration) {
            Bundle result = new Bundle();
            boolean value = performLoopAudio(inputText, outputFile, duration);
            result.putInt(RESULT, value ? RESULT_OK : RESULT_ERROR);
            return result;
        }

        @Override
        public Bundle mergeAudioVideo(String inputVideo, String inputAudio,
                                      String outputFile, boolean isRepeat) {
            Bundle result = new Bundle();
            boolean value = performMergeAudioVideo(inputVideo, inputAudio,
                    outputFile, isRepeat);
            result.putInt(RESULT, value ? RESULT_OK : RESULT_ERROR);
            return result;
        }

        @Override
        public Bundle compressFileSize(String inputFile, String outputFile,
                                       int videoWidth, int videoHeight, double birate) {
            Bundle result = new Bundle();
            boolean value = performCompressFileSize(inputFile, outputFile,
                    videoWidth, videoHeight, birate);
            result.putInt(RESULT, value ? RESULT_OK : RESULT_ERROR);
            return result;
        }

        @Override
        public Bundle addImageToVideo(String inputVideo, String[] inputImage,
                                      int numberImages, String outputFile, float[] x, float[] y,
                                      int[] width, int[] height, float[] rotation, float[] startTime,
                                      float[] endTime) {
            Bundle result = new Bundle();
            boolean value = performAddImageToVideo(inputVideo, inputImage,
                    numberImages, outputFile, x, y, width, height, rotation,
                    startTime, endTime);
            result.putInt(RESULT, value ? RESULT_OK : RESULT_ERROR);
            return result;
        }

        @Override
        public Bundle adjustVolume(String inputVideo, String outputVideo,
                                   float volume) {
            Bundle result = new Bundle();
            boolean value = performAdjustVolume(inputVideo, outputVideo, volume);
            result.putInt(RESULT, value ? RESULT_OK : RESULT_ERROR);
            return result;
        }

        @Override
        public Bundle mergeAudioVideoWithVolume(String inputVideo,
                                                String inputAudio, String outputFile, boolean isRepeat,
                                                float volume) {
            Bundle result = new Bundle();
            boolean value = performMergeAudioVideoWithVolume(inputVideo,
                    inputAudio, outputFile, isRepeat, volume);
            result.putInt(RESULT, value ? RESULT_OK : RESULT_ERROR);
            return result;
        }

        @Override
        public Bundle addTextToVideo(String inputVideo, String[] inputText,
                                     int numberTexts, String outputFile, String[] textFonts,
                                     int[] textSizes, String[] textColors, String[] backgrounds,
                                     float[] starts, float[] ends, float[] x, float[] y) {
            Bundle result = new Bundle();
            boolean value = performAddTextToVideo(inputVideo, inputText,
                    numberTexts, outputFile, textFonts, textSizes, textColors,
                    backgrounds, starts, ends, x, y);
            result.putInt(RESULT, value ? RESULT_OK : RESULT_ERROR);
            return result;
        }

        @Override
        public Bundle resizeVideo(String inputVideo, String outputVideo, int width, int height){
            Bundle result = new Bundle();
            boolean value = performResizeVideo(inputVideo, outputVideo, width, height);
            result.putInt(RESULT, value ? RESULT_OK : RESULT_ERROR);
            return result;
        }

        @Override
        public Bundle getVideoInfo(String inputVideo, String outputFile){
            Bundle result = new Bundle();
            boolean value = performGetVideoInfo(inputVideo, outputFile);
            result.putInt(RESULT, value ? RESULT_OK : RESULT_ERROR);
            return result;
        }
    };

    public boolean performGetVideoInfo(String inputVideo, String outputFile){
        Log.e("Az Plugin: ", "Get to Az plugin");
        LinkedList<String> command = new LinkedList<>();

        command.add(mFfmpegPath);
        command.add("-i");
        command.add(inputVideo);

        boolean value = executeFFmpegCommand(command);
        writeToFile(new File(outputFile), mAllLog);
        return value;
    }

    public boolean performResizeVideo(String inputVideo, String outputVideo, int width, int height){
        LinkedList<String> command = new LinkedList<>();
        String filter = "scale="+width+":"+height+",setsar=1:1";
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(inputVideo);
        int inputWidth = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int inputHeight = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        boolean resize = (inputWidth!=width) || (inputHeight!=height);

        command.add(mFfmpegPath);
        command.add("-i");
        command.add(inputVideo);
        if (resize){
            command.add("-vf");
            command.add(filter);
        }
        command.add("-threads");
        command.add("5");
        command.add("-video_track_timescale");
        command.add("90k");
        command.add("-qscale");
        command.add("15");
        command.add("-preset");
        command.add("ultrafast");
        command.add("-y");
        command.add(outputVideo);

        return executeFFmpegCommand(command);
    }

    public boolean performAddTextToVideo(String inputVideo, String[] inputText,
                                         int numberTexts, String outputFile, String[] textFonts,
                                         int[] textSizes, String[] textColors, String[] backgrounds,
                                         float[] starts, float[] ends, float[] x, float[] y) {
        LinkedList<String> command = new LinkedList<>();
        String filter = "";
        for (int i = 0; i < numberTexts; i++) {
            if (i == 0) {
                filter += "[0:v]";
            } else {
                filter += "[video" + i + "]";
            }
            filter += "drawtext=fontfile=" + textFonts[i] + ":textfile="
                    + inputText[i] + ":x=" + x[i] + ":y=" + y[i] + ":fontsize="
                    + textSizes[i] + ":fontcolor=" + textColors[i]
                    + ":box=1:boxcolor=" + backgrounds[i]
                    + ":boxborderw=10:enable='between(t," + starts[i] + ","
                    + ends[i] + ")'";
            if (i < numberTexts - 1) {
                filter += "[video" + (i + 1) + "];";
            } else {
                filter += "[out]";
            }
        }

        command.add(mFfmpegPath);
        command.add("-i");
        command.add(inputVideo);
        command.add("-filter_complex");
        command.add(filter);
        command.add("-map");
        command.add("[out]");
        command.add("-map");
        command.add("0:a");
        command.add("-preset");
        command.add("ultrafast");
        command.add("-y");
        command.add(outputFile);

        return executeFFmpegCommand(command);
    }

    public boolean performMergeAudioVideoWithVolume(String inputVideo,
                                                    String inputAudio, String outputFile, boolean isRepeat, float volume) {
        LinkedList<String> command = new LinkedList<>();
        String filter = "[1:a] volume=" + volume + "[out]";

        command.add(mFfmpegPath);
        command.add("-i");
        command.add(inputVideo);
        command.add("-i");
        command.add(inputAudio);
        command.add("-filter_complex");
        command.add(filter);
        command.add("-map");
        command.add("0:v");
        command.add("-map");
        command.add("[out]");
        command.add("-c:v");
        command.add("copy");
        command.add("-preset");
        command.add("ultrafast");
        if (isRepeat) {
            command.add("-shortest");
        }
        command.add("-bsf:a");
        command.add("aac_adtstoasc");
        command.add("-y");
        command.add(outputFile);

        return executeFFmpegCommand(command);
    }

    public boolean performAdjustVolume(String inputVideo, String outputVideo,
                                       float volume) {
        String volumeValue = "volume=" + volume;
        LinkedList<String> command = new LinkedList<>();
        command.add(mFfmpegPath);
        command.add("-i");
        command.add(inputVideo);
        command.add("-af");
        command.add(volumeValue);
        command.add("-preset");
        command.add("ultrafast");
        command.add("-y");
        command.add(outputVideo);

        return executeFFmpegCommand(command);
    }

    public boolean performAddImageToVideo(String inputVideo,
                                          String[] inputImages, int numberImages, String outputFile,
                                          float[] x, float[] y, int[] width, int[] height, float[] rotation,
                                          float[] startTime, float[] endTime) {
        LinkedList<String> command = new LinkedList<>();
        String filter = "";
        String overlay;
        String out;
        for (int i = 0; i < numberImages; i++) {
            overlay = "[" + (i + 1) + ":v]rotate=" + rotation[i]
                    + ":c=none,scale=" + width[i] + ":" + height[i]
                    + "[overlay" + (i + 1) + "];";
            filter += overlay;
        }
        for (int i = 0; i < numberImages; i++) {
            out = "[" + (i == 0 ? "0:v" : ("out" + i)) + "][overlay" + (i + 1)
                    + "]overlay=" + x[i] + ":" + y[i] + ":enable='between(t,"
                    + startTime[i] + "," + endTime[i] + ")'"
                    + (i < numberImages - 1 ? ("[out" + (i + 1) + "];") : "");
            filter += out;
        }
        command.add(mFfmpegPath);
        command.add("-i");
        command.add(inputVideo);
        for (int i = 0; i < numberImages; i++) {
            command.add("-i");
            command.add(inputImages[i]);
        }
        command.add("-filter_complex");
        command.add(filter);
        command.add("-pix_fmt");
        command.add("yuv420p");
        command.add("-preset");
        command.add("ultrafast");
        command.add("-video_track_timescale");
        command.add("90000");
        command.add("-c:a");
        command.add("copy");
        command.add("-y");
        command.add(outputFile);

        return executeFFmpegCommand(command);
    }

    public boolean performCompressFileSize(String inputFile, String outputFile,
                                           int videoWidth, int videoHeight, double birate) {
        LinkedList<String> command = new LinkedList<>();
        int height = videoHeight;
        int width = videoWidth;
        if (videoHeight % 2 == 1) {
            height = videoHeight - 1;
        }
        if (videoWidth % 2 == 1) {
            width = videoWidth - 1;
        }
        String filter = "scale=" + width + ":" + height;
        String videoBitrate = (int) (birate * 1024) + "k";

        command.add(mFfmpegPath);
        command.add("-i");
        command.add(inputFile);
        command.add("-vf");
        command.add(filter);
        command.add("-preset");
        command.add("ultrafast");
        command.add("-b");
        command.add(videoBitrate);
        command.add("-video_track_timescale");
        command.add("90000");
        command.add("-y");
        command.add(outputFile);

        return executeFFmpegCommand(command);
    }

    public boolean performMergeAudioVideo(String inputVideo, String inputAudio,
                                          String outputFile, boolean isRepeat) {
        LinkedList<String> command = new LinkedList<>();
        command.add(mFfmpegPath);
        command.add("-i");
        command.add(inputVideo);
        command.add("-i");
        command.add(inputAudio);
        command.add("-map");
        command.add("0:v");
        command.add("-map");
        command.add("1:a");
        command.add("-vcodec");
        command.add("copy");
        command.add("-acodec");
        command.add("copy");
        if (isRepeat) {
            command.add("-shortest");
        }
        command.add("-bsf:a");
        command.add("aac_adtstoasc");
        command.add("-y");
        command.add(outputFile);

        return executeFFmpegCommand(command);
    }

    public boolean performLoopAudio(String inputText, String outputFile,
                                    String duration) {
        LinkedList<String> command = new LinkedList<>();

        command.add(mFfmpegPath);
        command.add("-f");
        command.add("concat");
        command.add("-i");
        command.add(inputText);
        command.add("-c");
        command.add("copy");
        command.add("-t");
        command.add(duration);
        command.add("-y");
        command.add(outputFile);

        return executeFFmpegCommand(command);
    }

    public boolean performConvertToAAC(String inputFile, String outputFile,
                                       String start, String end, boolean isAAC) {
        LinkedList<String> command = new LinkedList<>();
        String codec = isAAC ? "copy" : "aac";
        command.add(mFfmpegPath);
        command.add("-i");
        command.add(inputFile);
        command.add("-ss");
        command.add(start);
        command.add("-to");
        command.add(end);
        command.add("-acodec");
        command.add(codec);
        command.add("-profile");
        command.add("aac_low");
        command.add("-preset");
        command.add("ultrafast");
        command.add("-y");
        command.add(outputFile);

        return executeFFmpegCommand(command);
    }

    private boolean performTrimVideo(String inputFile, String outputFile,
                                     String begin, String end) {

        LinkedList<String> command = new LinkedList<>();

        command.add(mFfmpegPath);
        command.add("-i");
        command.add(inputFile);
        command.add("-ss");
        command.add(begin);
        command.add("-to");
        command.add(end);
        command.add("-y");
        command.add("-c");
        command.add("copy");
        command.add("-copyts");
        command.add(outputFile);

        return executeFFmpegCommand(command);
    }

    private Boolean performCropVideo(String input, String output, int width,
                                     int height, int x, int y, int quality) {
        String filter = "crop=" + width + ":" + height + ":" + x + ":" + y;
        LinkedList<String> command = new LinkedList<>();

        command.add(mFfmpegPath);
        command.add("-i");
        command.add(input);
        command.add("-filter:v");
        command.add(filter);
        command.add("-y");
        command.add("-threads");
        command.add("5");
        command.add("-preset");
        command.add("ultrafast");
        command.add("-video_track_timescale");
        command.add("90000");
        command.add("-strict");
        command.add("-2");
        command.add("-crf");
        command.add(quality + "");
        command.add(output);

        return executeFFmpegCommand(command);
    }

    private boolean performConcatVideo(String inputFile, String outputFile) {
        LinkedList<String> command = new LinkedList<>();

        command.add(mFfmpegPath);
        command.add("-f");
        command.add("concat");
        command.add("-i");
        command.add(inputFile);
        command.add("-c");
        command.add("copy");
        command.add("-y");
        command.add(outputFile);

        return executeFFmpegCommand(command);
    }

    private boolean performConvertVideoToGif(String inputFile,
                                             String outputFile, int fps, int scale, int start, int duration,
                                             int loop) {
        String filter = "fps=" + fps + ",scale=" + scale
                + ":-1:flags=lanczos,palettegen";
        String palette = Environment.getExternalStorageDirectory() + "/.pal"
                + System.currentTimeMillis() + ".png";
        String startPoint = String.valueOf(((float) (start / 10)) / 100f);
        String durationInSeconds = String
                .valueOf(((float) (duration / 10)) / 100f);

        LinkedList<String> command = new LinkedList<>();

        command.add(mFfmpegPath);
        command.add("-v");
        command.add("warning");
        command.add("-ss");
        command.add(startPoint);
        command.add("-t");
        command.add(durationInSeconds);
        command.add("-i");
        command.add(inputFile);
        command.add("-vf");
        command.add(filter);
        command.add("-y");
        command.add(palette);
        if (!executeFFmpegCommand(command)) {
            return false;
        }

        filter = "fps=" + fps + ",scale=" + scale
                + ":-1:flags=lanczos [x]; [x][1:v] paletteuse";
        command = new LinkedList<>();
        command.add(mFfmpegPath);
        command.add("-v");
        command.add("warning");
        command.add("-ss");
        command.add(startPoint);
        command.add("-t");
        command.add(durationInSeconds);
        command.add("-i");
        command.add(inputFile);
        command.add("-i");
        command.add(palette);
        command.add("-lavfi");
        command.add(filter);
        command.add("-loop");
        command.add(String.valueOf(loop));
        command.add("-y");
        command.add(outputFile);
        boolean result = executeFFmpegCommand(command);
        new File(palette).delete();
        return result;
    }

    public static boolean executeFFmpegCommand(LinkedList<String> command) {
        // Execute ffmpeg

        Process ffmpegProcess;
        ProcessBuilder procBuilder = new ProcessBuilder(command);
        mAllLog = "";
        try {
            ffmpegProcess = procBuilder.redirectErrorStream(true).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    ffmpegProcess.getInputStream()));
            System.out
                    .println("***Starting FFMPEG***" + procBuilder.toString());
            while ((mLineLog = reader.readLine()) != null) {
                System.out.println("***" + mLineLog + "***");
                mAllLog += mLineLog +"\n";
            }
            System.out.println("***Ending FFMPEG***");

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        ffmpegProcess.destroy();
        return true;
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

    public String performGetLineLog() {
        return mLineLog;
    }

    public static boolean initFFMPEG(Context ctx) {

        System.out.println("cpu info: " + getCpuInfo());
        try {
            InputStream ffmpegInputStream = ctx.getAssets().open(
                    getCpuInfo() + "/" + FFMPEG);

            mFfmpegPath = ctx.getApplicationInfo().dataDir + "/" + FFMPEG;

            File destinationFile = new File(mFfmpegPath);

            OutputStream destinationOS = new BufferedOutputStream(
                    new FileOutputStream(destinationFile));
            int numRead;
            byte[] buf = new byte[1024];
            while ((numRead = ffmpegInputStream.read(buf)) >= 0) {
                destinationOS.write(buf, 0, numRead);
            }

            destinationOS.flush();
            destinationOS.close();

            try {
                String[] args = { "/system/bin/chmod", "755", mFfmpegPath };
                Process process = new ProcessBuilder(args).start();
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                process.destroy();

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    public static String getCpuInfo() {
        if (Build.CPU_ABI.equals(CPU_X86)
                || Build.CPU_ABI.equals(CPU_ARMEABI_V7A)
                || Build.CPU_ABI.equals(CPU_ARMEABI_V7A_NEON)) {
            return Build.CPU_ABI;
        } else {
            return CPU_ARMEABI_V7A;
        }
    }

    private void checkUpdateVersion() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        if (!sharedPrefs.contains(getResources().getString(
                R.string.pref_app_version_code))) {
            try {
                PackageInfo packageInfo = getApplication().getPackageManager()
                        .getPackageInfo(getApplication().getPackageName(), 0);
                sharedPrefs
                        .edit()
                        .putInt(getString(R.string.pref_app_version_code),
                                packageInfo.versionCode).apply();
            } catch (NotFoundException | NameNotFoundException e) {
                e.printStackTrace();
            }

        } else {
            int oldVersionCode = sharedPrefs
                    .getInt(getResources().getString(
                            R.string.pref_app_version_code), 1);
            int newVersionCode;
            try {
                PackageInfo packageInfo = getApplication().getPackageManager()
                        .getPackageInfo(getApplication().getPackageName(), 0);
                newVersionCode = packageInfo.versionCode;
                if (oldVersionCode != newVersionCode) {

                    sharedPrefs
                            .edit()
                            .putInt(getString(R.string.pref_app_version_code),
                                    newVersionCode).apply();

                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}