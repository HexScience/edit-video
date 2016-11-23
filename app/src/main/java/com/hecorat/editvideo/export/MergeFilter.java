package com.hecorat.editvideo.export;

import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;

import java.util.ArrayList;

/**
 * Created by TienDam on 11/23/2016.
 */

public class MergeFilter {
    public static String getFilter(ArrayList<ExportTask.VideoHolder> listVideo, int order){
        String filter="";
        String in = "";
        for (int i=0; i<listVideo.size(); i++){
            int index = i+order;
            ExportTask.VideoHolder video = listVideo.get(i);
            filter += prepareVideo(video, index);
            in += "[v"+index+"]["+index+":a:0]";
        }
        filter += in+"concat=n="+listVideo.size()+":v=1:a=1[v][a];";
        return filter;
    }

    private static String prepareVideo(ExportTask.VideoHolder video, int index){
        String filter="";
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(video.videoPath);
        int height = Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        boolean scale = height>720;
        String in = "["+index+":v]";
        if (scale){
            filter += in+"scale=-1:720[v_scale];";
            in = "[v_scale]";
        }
        filter += "[0:v]"+in+"overlay=(main_w-overlay_w)/2:" +
                "(main_h-overlay_h)/2:shortest=1[v"+index+"];";
        return filter;
    }
}
