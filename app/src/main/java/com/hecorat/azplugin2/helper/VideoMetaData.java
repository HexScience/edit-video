package com.hecorat.azplugin2.helper;

import android.content.Context;
import android.util.Log;

import com.hecorat.azplugin2.export.FFmpeg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by bkmsx on 21/09/2016.
 */
public class VideoMetaData {
    public boolean hasAudio;
    private boolean videoFirst;
    private String codecVideo, tbnVideo, bitrateVideo, framerateVideo, width, height;
    private  String bitrateAudio, sampleAudio, codecAudio, audioChannels;
    private static String fileString;


    public static VideoMetaData getMetaData(Context context, String videoPath){
        String fileLog = Utils.getTempFolder()+"/logFile.txt";
        FFmpeg.getInstance(context).performGetVideoInfo(videoPath, fileLog);
        try {
            fileString = getStringFromFile(fileLog);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return getTrack();
    }

    private static VideoMetaData getTrack() {
        VideoMetaData videoMetaData = new VideoMetaData();
        try {
            String track0String = fileString.substring(fileString.indexOf("Stream"));
            int track0Start = track0String.indexOf("Stream");
            int track0End = track0String.indexOf("Metadata");
            String track0 = track0String.substring(track0Start, track0End);
            String track0LC = track0.toLowerCase();

            String track1String = track0String.substring(track0End);
            String track1, track1LC;
            String trackVideo, trackAudio = "";

            int track1Start = track1String.indexOf("Stream");
            if (track1Start < 0) {
                videoMetaData.hasAudio = false;
                videoMetaData.videoFirst = true;
                trackVideo = track0LC;
            } else {
                videoMetaData.hasAudio = true;
                int track1End = track1String.indexOf("Metadata", track1Start);
                track1 = track1String.substring(track1Start, track1End);
                track1LC = track1.toLowerCase();
                if (track0LC.indexOf("video") > 0) {
                    videoMetaData.videoFirst = true;
                    trackVideo = track0LC;
                    trackAudio = track1LC;
                } else {
                    videoMetaData.videoFirst = false;
                    trackVideo = track1LC;
                    trackAudio = track0LC;
                }
            }

            String[] videoCharac = trackVideo.split(",");

            int codecIndex = trackVideo.indexOf("video") + 7;
            int codecEndIndex = trackVideo.indexOf(" ", codecIndex);
            videoMetaData.codecVideo = trackVideo.substring(codecIndex, codecEndIndex);

            for (int i = 1; i < videoCharac.length; i++) {
                if (videoCharac[i].indexOf("x") > 0) {
                    int xPosition = videoCharac[i].indexOf("x");
                    videoMetaData.width = videoCharac[i].substring(1, xPosition);
                    int heightEnd = videoCharac[i].indexOf(" ", xPosition) > 0 ? videoCharac[i].indexOf(" ", xPosition) : videoCharac[i].length();
                    videoMetaData.height = videoCharac[i].substring(xPosition + 1, heightEnd);
                }
                if (videoCharac[i].indexOf("tbn") > 0) {
                    int tbn = videoCharac[i].indexOf("tbn");
                    videoMetaData.tbnVideo = videoCharac[i].substring(1, tbn - 1);
                }

                if (videoCharac[i].indexOf("kb/s") > 0) {
                    int kbs = videoCharac[i].indexOf("kb/s");
                    videoMetaData.bitrateVideo = videoCharac[i].substring(1, kbs - 1);
                }

                if (videoCharac[i].indexOf("fps") > 0) {
                    int fps = videoCharac[i].indexOf("fps");
                    videoMetaData.framerateVideo = videoCharac[i].substring(1, fps - 1);
                }
            }

            if (!videoMetaData.hasAudio) {
                return videoMetaData;
            }

            String[] audioCharacs = trackAudio.split(",");

            int audioIndex = trackAudio.indexOf("audio") + 7;
            int audioEndIndex = trackAudio.indexOf(" ", audioIndex);
            videoMetaData.codecAudio = trackAudio.substring(audioIndex, audioEndIndex);

            int audioChannelsIndex = trackAudio.indexOf("hz") + 4;
            int audioChannelsEnd = trackAudio.indexOf(",", audioChannelsIndex);
            videoMetaData.audioChannels = trackAudio.substring(audioChannelsIndex, audioChannelsEnd);

            for (String audioCharac : audioCharacs) {
                if (audioCharac.indexOf("hz") > 0) {
                    int hz = audioCharac.indexOf("hz");
                    videoMetaData.sampleAudio = audioCharac.substring(1, hz - 1);
                }
                if (audioCharac.indexOf("kb/s") > 0) {
                    int kbs = audioCharac.indexOf("kb/s");
                    videoMetaData.bitrateAudio = audioCharac.substring(1, kbs - 1);
                }
            }

            return videoMetaData;
        } catch (StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    public static void log(String msg) {
        Log.e("Text Analize", msg);
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }
}
