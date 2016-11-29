package com.hecorat.editvideo.export;

import com.hecorat.editvideo.timeline.AudioTL;

import java.util.ArrayList;

/**
 * Created by TienDam on 11/23/2016.
 */

public class AudioFilter {
    public static String getFilter(float videoVolume, String output, ArrayList<AudioTL> listAudio, int order){
        String filter ="";
        String in = "[a0]";
        filter += setVolume(videoVolume, 0, false);
        for (int i=0; i<listAudio.size(); i++){
            AudioHolder audio = listAudio.get(i).audioHolder;
            filter += prepareAudio(audio.startInTimeLine, i+order);
            filter += setVolume(audio.volume, i+order, true);
            in += "[auv"+(i+order)+"]";
        }
        filter += in + "amix=inputs="+(listAudio.size()+1)+":duration=longest" +
                ":dropout_transition=1"+output;
        return filter;
    }

    private static String prepareAudio(float startTime, int index){
        return "aevalsrc=0:d="+startTime+"[s1];[s1]["+index+":a]concat=n=2:v=0:a=1[au"+index+"];";
    }

    private static String setVolume(float volume, int index, boolean isAudio){
        String in = isAudio?"[au"+index+"]":"[a]";
        String out = isAudio?"[auv"+index+"];":"[a0];";
        return in+"aformat=sample_fmts=fltp:sample_rates=" +
                "44100:channel_layouts=stereo,volume="+volume+out;
    }
}
