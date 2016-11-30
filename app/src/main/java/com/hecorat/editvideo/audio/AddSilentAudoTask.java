package com.hecorat.editvideo.audio;

import android.content.Context;
import android.os.AsyncTask;

import com.hecorat.editvideo.export.FFmpeg;
import com.hecorat.editvideo.helper.Utils;
import com.hecorat.editvideo.helper.VideoMetaData;
import com.hecorat.editvideo.timeline.VideoTL;

import java.util.LinkedList;

/**
 * Created by TienDam on 11/29/2016.
 */

public class AddSilentAudoTask extends AsyncTask<VideoTL, Void, Void>{
    Context context;
    public AddSilentAudoTask(Context context){
        this.context = context;
    }
    @Override
    protected Void doInBackground(VideoTL... params) {
        VideoTL videoTL = params[0];
        VideoMetaData videoMetaData = VideoMetaData.getMetaData(context, videoTL.videoPath);
        if (!videoMetaData.hasAudio){
            String outPath = Utils.getTempFolder()+"/"+System.currentTimeMillis()+".mp4";
            addSilentAudio(videoTL.videoPath, outPath);
            videoTL.videoPath = outPath;
            videoTL.audioPreview = outPath;
            videoTL.hasAudio = false;
        }
        return null;
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
