package com.hecorat.editvideo.helper;

import android.content.Context;
import android.util.Log;

import com.hecorat.editvideo.export.FFmpeg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by bkmsx on 21/09/2016.
 */
public class VideoMetaData {
    public boolean videoFirst, hasAudio;
    public String codecVideo, tbnVideo, bitrateVideo, framerateVideo, width, height;
    public String bitrateAudio, sampleAudio, codecAudio, audioChannels;
    static String fileString;
    public static final int VIDEO_CODEC = 0;
    public static final int VIDEO_TBN = 1;
    public static final int AUDIO_CODEC = 2;
    public static final int WIDTH = 3;
    public static final int HEIGHT = 4;
    public static final int VIDEO_FIRST = 5;


    public static VideoMetaData getMetaData(Context context, String videoPath){
        String fileLog = Utils.getTempFolder()+"/logFile.txt";
        FFmpeg.getInstance(context).performGetVideoInfo(videoPath, fileLog);
        try {
            fileString = getStringFromFile(fileLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
        VideoMetaData videoMetaData = getTrack();
        logData(videoMetaData);
        return videoMetaData;
    }

    public boolean cmp(VideoMetaData metaData, int property) {
        switch (property) {
            case VIDEO_CODEC:
                return codecVideo.equals(metaData.codecVideo);
            case VIDEO_TBN:
                return tbnVideo.equals(metaData.tbnVideo);
            case WIDTH:
                return width.equals(metaData.width);
            case HEIGHT:
                return height.equals(metaData.height);
            case AUDIO_CODEC:
                return codecAudio.equals(metaData.codecAudio);
            case VIDEO_FIRST:
                return videoFirst == metaData.videoFirst;
            default:
                return false;
        }
    }

    public static VideoMetaData getTrack() {
        VideoMetaData videoMetaData = new VideoMetaData();

        String track0String = fileString.substring(fileString.indexOf("Stream"));
        int track0Start = track0String.indexOf("Stream");
        int track0End = track0String.indexOf("Metadata");
        String track0 = track0String.substring(track0Start, track0End);
        String track0LC = track0.toLowerCase();

        String track1String = track0String.substring(track0End);
        String track1, track1LC = "";
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
            if (track0LC.indexOf("video") > 0){
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

        for (int i=1; i<videoCharac.length; i++){
//            log(videoCharac[i]);

            if (videoCharac[i].indexOf("x")>0){
                int xPosition = videoCharac[i].indexOf("x");
                videoMetaData.width = videoCharac[i].substring(1, xPosition);
                int heightEnd = videoCharac[i].indexOf(" ", xPosition)>0?videoCharac[i].indexOf(" ", xPosition):videoCharac[i].length();
                videoMetaData.height = videoCharac[i].substring(xPosition+1, heightEnd);
            }
            if (videoCharac[i].indexOf("tbn")>0){
                int tbn = videoCharac[i].indexOf("tbn");
                videoMetaData.tbnVideo = videoCharac[i].substring(1, tbn-1);
            }

            if (videoCharac[i].indexOf("kb/s")>0){
                int kbs = videoCharac[i].indexOf("kb/s");
                videoMetaData.bitrateVideo = videoCharac[i].substring(1, kbs-1);
            }

            if (videoCharac[i].indexOf("fps")>0){
                int fps = videoCharac[i].indexOf("fps");
                videoMetaData.framerateVideo = videoCharac[i].substring(1, fps-1);
            }
        }

        if (!videoMetaData.hasAudio) {
            return videoMetaData;
        }

        String[] audioCharac = trackAudio.split(",");

        int audioIndex = trackAudio.indexOf("audio")+7;
        int audioEndIndex = trackAudio.indexOf(" ", audioIndex);
        videoMetaData.codecAudio = trackAudio.substring(audioIndex, audioEndIndex);

        int audioChannelsIndex = trackAudio.indexOf("hz")+4;
        int audioChannelsEnd = trackAudio.indexOf(",", audioChannelsIndex);
        videoMetaData.audioChannels = trackAudio.substring(audioChannelsIndex, audioChannelsEnd);

        for (int i=0; i<audioCharac.length; i++) {
//            log(audioCharac[i]);
            if (audioCharac[i].indexOf("hz")>0){
                int hz = audioCharac[i].indexOf("hz");
                videoMetaData.sampleAudio = audioCharac[i].substring(1, hz-1);
            }
            if (audioCharac[i].indexOf("kb/s")>0){
                int kbs = audioCharac[i].indexOf("kb/s");
                videoMetaData.bitrateAudio = audioCharac[i].substring(1, kbs -1);
            }
        }

        return videoMetaData;
    }

    public static void logData(VideoMetaData videoMetaData){
        log("Video first: "+videoMetaData.videoFirst);
        log("Has audio: "+videoMetaData.hasAudio);
        log("Video Codec: "+videoMetaData.codecVideo);
        log("Video Tbn: "+videoMetaData.tbnVideo);
        log("Video Width: "+videoMetaData.width);
        log("Video Height: "+videoMetaData.height);
        log("Video Bitrate: "+videoMetaData.bitrateVideo);
        log("Video Framerate: "+videoMetaData.framerateVideo);
        log("Audio Codec: "+videoMetaData.codecAudio);
        log("Audio Channel: "+videoMetaData.audioChannels);
        log("Audio Bitrate: "+ videoMetaData.bitrateAudio);
        log("Audio Sample: "+videoMetaData.sampleAudio);
    }

    public static void log(String msg) {
        Log.e("Text Analize", msg);
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }
}
