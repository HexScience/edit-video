package com.hecorat.azplugin2.audio;

import android.content.Context;
import android.os.AsyncTask;

import com.hecorat.azplugin2.export.FFmpeg;
import com.hecorat.azplugin2.helper.Utils;
import com.hecorat.azplugin2.helper.VideoMetaData;
import com.hecorat.azplugin2.timeline.VideoTL;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by TienDam on 11/29/2016.
 */

public class AddSilentAudoTask extends AsyncTask<Void, Void, Void>{
    Context context;
    boolean inRestore;
    VideoTL videoTL;
    ArrayList<VideoTL> listVideo;
    public AddSilentAudoTask(Context context, VideoTL videoTL){
        this.context = context;
        this.videoTL = videoTL;
        inRestore = false;
    }

    public AddSilentAudoTask(Context context, ArrayList<VideoTL> listVideo){
        this.context = context;
        this.listVideo = listVideo;
        inRestore = true;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (inRestore) {
            for (VideoTL videoTL : listVideo) {
                fixOne(videoTL);
            }
        } else {
            fixOne(videoTL);
        }
        return null;
    }

    private void fixOne(VideoTL videoTL) {
        VideoMetaData videoMetaData = VideoMetaData.getMetaData(context, videoTL.videoPath);
        if (!videoMetaData.hasAudio){
            String outPath = Utils.getTempFolder()+"/"+System.currentTimeMillis()+".mp4";
            addSilentAudio(videoTL.videoPath, outPath);
            videoTL.videoPath = outPath;
            videoTL.audioPreview = outPath;
            videoTL.hasAudio = false;
        }
    }

    private void addSilentAudio(String videoPath, String outPath){
        LinkedList<String> command = new LinkedList<>();
        command.add("-i");
        command.add(videoPath);
        command.add("-f");
        command.add("lavfi");
        command.add("-i");
        command.add("anullsrc");
        command.add("-c:v");
        command.add("copy");
        command.add("-map");
        command.add("0:v");
        command.add("-map");
        command.add("1:a");
        command.add("-shortest");
        command.add("-y");
        command.add(outPath);
        FFmpeg.getInstance(context).executeFFmpegCommand(command);
    }
}
