package com.hecorat.azplugin2.export;

import com.hecorat.azplugin2.timeline.VideoTL;

import java.util.ArrayList;

/**
 * Created by TienDam on 11/23/2016.
 */

class MergeFilter {
    static String getFilter(ArrayList<VideoTL> listVideo, int quality, int order){
        String filter="";
        String in = "";
        for (int i=0; i<listVideo.size(); i++){
            int index = i+order;
            VideoHolder video = listVideo.get(i).videoHolder;
            filter += prepareVideo(video, quality, index);
            in += "[v"+index+"][a"+index+"]";
        }
        filter += in+"concat=n="+listVideo.size()+":v=1:a=1[v][a];";
        return filter;
    }

    private static String prepareVideo(VideoHolder video, int quality, int index){
        String filter="";
        String in = "["+index+":v]";
        int height = quality;
        int width = height * 16 / 9;
        int widthScale = -1;
        if (video.ratio > 16/9) {
            widthScale = width;
        }
        filter += in + "crop=" + video.width + ":" + video.height +
                ":" + video.left + ":" + video.top + "[crop];";
        in = "[crop]";
        filter += in + "scale=" + widthScale +":"+quality+"[v_scale];";
        in = "[v_scale]";
        filter += "color=black:" + width + "x" +height + ", fps=30[bgr0];" +
                "[bgr0][0:v]overlay[bgr];";

        filter += "[bgr]"+in+"overlay=(main_w-overlay_w)/2:" +
                "(main_h-overlay_h)/2:shortest=1[v"+index+"];";

        filter += "["+index+":a]"+"aformat=sample_fmts=fltp:sample_rates=44100" +
                ":channel_layouts=stereo,volume="+video.volume+"[a"+index+"];";

        return filter;
    }
}
