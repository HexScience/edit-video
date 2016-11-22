package com.hecorat.editvideo.export;

import android.content.Context;
import android.os.Build;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.LinkedList;

/**
 * Created by TienDam on 11/22/2016.
 */

public class FFmpeg {
    private static final String CPU_X86 = "x86";
    private static final String CPU_ARMEABI_V7A = "armeabi-v7a";
    private static final String CPU_ARMEABI_V7A_NEON = "armeabi-v7a-neon";
    private static final String FFMPEG = "ffmpeg";
    public static String mFfmpegPath;
    private static final String RESULT = "result";
    private static final int RESULT_OK = 1;
    private static final int RESULT_ERROR = 0;

    public static boolean executeFFmpegCommand(Context context, LinkedList<String> command) {
        initFFMPEG(context);
        command.add(0, mFfmpegPath);
        Process ffmpegProcess = null;
        ProcessBuilder procBuilder = new ProcessBuilder(command);
        try {
            ffmpegProcess = procBuilder.redirectErrorStream(true).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    ffmpegProcess.getInputStream()));
            System.err
                    .println("***Starting FFMPEG***" + procBuilder.toString());
            String mLineLog;
            while ((mLineLog = reader.readLine()) != null) {
                System.err.println("***" + mLineLog + "***");
            }
            System.err.println("***Ending FFMPEG***");

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        if (ffmpegProcess != null) {
            ffmpegProcess.destroy();
        }
        return true;
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
}
