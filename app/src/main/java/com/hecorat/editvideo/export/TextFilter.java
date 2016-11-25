package com.hecorat.editvideo.export;

import android.os.Environment;

import com.hecorat.editvideo.timeline.ExtraTimeLine;

import java.util.ArrayList;

/**
 * Created by TienDam on 11/23/2016.
 */

public class TextFilter {
    public static String getFilter(String input, String output,
                                   ArrayList<ExtraTimeLine> listText){
        String textPath = Environment.getExternalStorageDirectory()+"/text.txt";
        String textFont = Environment.getExternalStorageDirectory()+"/font.otf";
        String filter="";
        for (int i=0; i<listText.size(); i++){
            TextHolder text = listText.get(i).textHolder;
            String in = i==0? input:"[out"+i+"]";
            String out = i==listText.size()-1?output:"[out"+(i+1)+"];";
            filter += "color=black@0:400x100,fps=30[bgr];" +
                    "[bgr]format=rgba,drawtext=fontfile="+textFont+":textfile="+textPath+
                    ":fontsize="+text.size+":fontcolor="+text.fontColor+
                    ":box=1:boxcolor="+text.boxColor+",colorkey=000000:0.01:1[text];"+
                    "[text]rotate="+text.rotate+":c=none:ow=rotw("+text.rotate+")"+
                    ":oh=roth("+text.rotate+")[ov];"+
                    in+"[ov]overlay=x="+text.x+":y="+text.y+":shortest=1"+out;
        }
        return filter;
    }
}
